package com.example.budgetplanning;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GestureDetectorCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class TransactionConstructorActivityTest extends AppCompatActivity {

    private BottomAppBar mBottomAppBar;
    private ConstraintLayout mBottomSheet;
    private BottomSheetBehavior mBottomSheetBehavior;
    private SurfaceView barCodeCameraView;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private ProgressBar loadingAssetsProgressBar;
    private int AssetsRecyclerViewScrollPosition, currentActiveAsset;
    private LinearLayoutManager mLayoutManager;
    private AutoCompleteTextView productNamesTextView, productQuantityTypesTextView;
    private EditText productQuantityTextView;
    private String currentBarcode;
    private ChipGroup categoryChips, dateChips;
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    private GestureDetectorCompat mDetector;
    private DatabaseReference productDatabase, assetDatabase;
    private ArrayList<String> productNamesList;
    private ArrayList<Product> productList;
    private TextInputLayout productNameLayout, productQuantityLayout;
    private ArrayList<FinancialAsset> listOfAssets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_constructor_test);
        bindViews();
        adjustTextFieldsToScreenSize();
        setUpAutocompleteTextView();
        setUpRecyclerView();
        initializeDetectorsAndSources();
        setUpCategoriesInChips();
        setUpDateChips();
        productQuantityTypesTextView.setText(getResources().getStringArray(R.array.typesOfQuantity)[0]);
        productNamesTextView.requestFocus();
        mDetector = new GestureDetectorCompat(this, new SwipeGestureListener());
        loadProductsFromDatabase();
        loadAssetsFromDatabase();

        mBottomAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });
    }

    private void setUpCategoriesInChips() {
        String[] categories = getResources().getStringArray(R.array.categories);
        for (String category: categories) {
            Chip chip = new Chip(this);
            chip.setText(category);
            chip.setEnsureMinTouchTargetSize(false);
            chip.setChipDrawable(ChipDrawable.createFromAttributes(this, null, 0, R.style.Widget_MaterialComponents_Chip_Choice ));
            if (category.equals(categories[0])) {
                chip.setChecked(true);
            }
            categoryChips.addView(chip);
        }
    }

    private void setUpDateChips() {
        Calendar calendar = Calendar.getInstance();
        DateQueryHelper dateHelper = new DateQueryHelper();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        Calendar newCalendar = new GregorianCalendar(currentYear, currentMonth, 1);
        for (int i=0; i<10; i++) {
            Chip chip = new Chip(this);
            chip.setText(dateHelper.dateParseToString(currentYear,currentMonth,currentDay));
            chip.setEnsureMinTouchTargetSize(false);
            chip.setChipDrawable(ChipDrawable.createFromAttributes(this, null, 0, R.style.Widget_MaterialComponents_Chip_Choice ));
            dateChips.addView(chip);
            if (currentDay>1) {
                currentDay-=1;
            } else {
                currentMonth -=1;
                newCalendar = new GregorianCalendar(currentYear, currentMonth, 1);
                currentDay = newCalendar.getActualMaximum(calendar.DAY_OF_MONTH);
            }
            if (i==0) {
                chip.setChecked(true);
            }
        }
    }

    private void loadProductsFromDatabase() {
        productDatabase = FirebaseDatabase.getInstance().getReference().child("Продукты");
        productDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productList = new ArrayList<Product>();
                productNamesList = new ArrayList<String>();
                for (DataSnapshot currentSnapshot : snapshot.getChildren()) {
                    Product product = currentSnapshot.getValue(Product.class);
                    productList.add(product);
                    if (product.getDefaultQuantity() > 0) {
                        productNamesList.add(product.getName()+" "+product.getManufacturer()+" "+ product.getDefaultQuantity()+product.getDefaultQuantityType());
                    }
                    else {
                        productNamesList.add(product.getName()+" "+product.getManufacturer());
                    }
                }
                setUpAutocompleteTextView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadAssetsFromDatabase() {
        assetDatabase = FirebaseDatabase.getInstance().getReference().child("Активы");
        assetDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listOfAssets.clear();
                for (DataSnapshot currentSnapshot : snapshot.getChildren()) {
                    FinancialAsset currentAsset = currentSnapshot.getValue(FinancialAsset.class);
                    if (currentAsset.getTypeOfAsset().equals("card")) {
                        FinancialAssetCard currentCard = currentSnapshot.getValue(FinancialAssetCard.class);
                        listOfAssets.add(currentCard);
                    }
                    if (currentAsset.getTypeOfAsset().equals("cash")) {
                        FinancialAssetCash currentCash = currentSnapshot.getValue(FinancialAssetCash.class);
                        listOfAssets.add(currentCash);
                    }
                }
                mAdapter.notifyDataSetChanged();
                if (listOfAssets.size()>0) {
                    currentActiveAsset=0;
                }
                loadingAssetsProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void initializeDetectorsAndSources() {
        barcodeDetector = new BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.ALL_FORMATS).build();
        cameraSource = new CameraSource.Builder(this, barcodeDetector).setRequestedPreviewSize(1920,1080).setAutoFocusEnabled(true).build();
        barCodeCameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(TransactionConstructorActivityTest.this, Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED)
                    {
                        cameraSource.start(barCodeCameraView.getHolder());
                    }
                    else {
                        ActivityCompat.requestPermissions(TransactionConstructorActivityTest.this, new String[] {Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                    }
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() !=0) {
                    barCodeCameraView.post(new Runnable() {
                        @Override
                        public void run() {
                            if (barcodes.valueAt(0).email!=null) {
                                currentBarcode =String.valueOf(barcodes.valueAt(0).email.address);
                                Toast.makeText(getApplicationContext(), String.valueOf(barcodes.valueAt(0).email.address), Toast.LENGTH_LONG).show();
                            }
                            else {
                                if (!barcodes.valueAt(0).displayValue.equals(currentBarcode)) {
                                    Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                    v.vibrate(500);
                                }
                                currentBarcode=barcodes.valueAt(0).displayValue;
                                Toast.makeText(getApplicationContext(), String.valueOf(barcodes.valueAt(0).displayValue), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
    }

    private void setUpAutocompleteTextView () {
        ArrayAdapter<String> namesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, productNamesList);
        productNamesTextView.setAdapter(namesAdapter);
        String[] productQuantityTypesList = getResources().getStringArray(R.array.typesOfQuantity);
        ArrayAdapter<String> quantityTypesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, productQuantityTypesList);
        productQuantityTypesTextView.setAdapter(quantityTypesAdapter);
    }

    private void setUpRecyclerView() {
        listOfAssets = new ArrayList<FinancialAsset>();
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false);
        mAdapter = new FinancialAssetAdapter(listOfAssets);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        AssetsRecyclerViewScrollPosition = 0;
        currentActiveAsset =-1;
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState==0) {
                    for (int i = 0; i < listOfAssets.size(); i++) {
                        if ((AssetsRecyclerViewScrollPosition>i*mRecyclerView.computeHorizontalScrollExtent()-mRecyclerView.computeHorizontalScrollExtent()/2) &&
                                (AssetsRecyclerViewScrollPosition<i*mRecyclerView.computeHorizontalScrollExtent()+mRecyclerView.computeHorizontalScrollExtent()/2)) {
                            mLayoutManager.scrollToPosition(i);
                            currentActiveAsset=i;
                            AssetsRecyclerViewScrollPosition=i*mRecyclerView.computeHorizontalScrollExtent();
                            break;
                        }
                    }
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                AssetsRecyclerViewScrollPosition += dx;
                }
        });
    }

    private void bindViews() {
        mBottomSheet = findViewById(R.id.TrC_bottom_sheet);
        mBottomAppBar = findViewById(R.id.TrC_bottom_app_bar);
        mBottomSheetBehavior = BottomSheetBehavior.from(mBottomSheet);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        productNameLayout = (TextInputLayout) findViewById(R.id.TrC_productNameLayout);
        productQuantityLayout = (TextInputLayout) findViewById(R.id.TrC_productQuantityLayout);
        productNamesTextView = (AutoCompleteTextView) findViewById(R.id.TrC_productNamesAutocomplete);
        productQuantityTextView = (EditText) findViewById(R.id.TrC_productQuantityEditText);
        productQuantityTypesTextView = (AutoCompleteTextView) findViewById(R.id.TrC_productQuantityTypeAutocomplete);
        categoryChips = (ChipGroup) findViewById(R.id.TrC_categoryChipGroup);
        dateChips = (ChipGroup) findViewById(R.id.TrC_dateChipGroup);
        barCodeCameraView = (SurfaceView) findViewById(R.id.TrC_BarCodeSurfaceView);
        mRecyclerView = (RecyclerView) findViewById(R.id.TrC_sourceOfTransactionRecyclerView);
        loadingAssetsProgressBar = (ProgressBar) findViewById(R.id.TrC_LoadingAssetsProgressBar);
    }

    private void adjustTextFieldsToScreenSize() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int height = metrics.heightPixels;
        if (height < 1600) {
            productNameLayout.setHintEnabled(false);
            productQuantityLayout.setHintEnabled(false);
        }
    }

    @Override
    public boolean onTouchEvent (MotionEvent event) {
        if (this.mDetector.onTouchEvent(event)) {
            if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
            else if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        }
        return super.onTouchEvent(event);
    }

    class SwipeGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent event) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
            return true;
        }
    }

}