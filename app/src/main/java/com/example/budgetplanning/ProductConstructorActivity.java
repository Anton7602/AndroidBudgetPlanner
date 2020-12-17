package com.example.budgetplanning;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;

public class ProductConstructorActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private SurfaceView barCodeCameraView;
    private EditText barCodeOutputTextView, productNameEditText, productQuantityEditText, productManufacturer;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private Button submitProductToDatabaseBtn;
    private ImageButton  acceptCurrentBarCodeBtn;
    private Spinner productCategories, productQuantitiesTypes;
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    private DatabaseReference productDatabase;
    private ValueEventListener repetativityChecker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_constructor);
        bindViews();
        setUpSpinners();
        initializeDetectorsAndSources();
        final Intent intent = getIntent();
        try
        {
            if (intent.getStringExtra(ShoppingListActivity.EXTRA_PRODUCT_NAME).length()>1)
            {
                productNameEditText.setText(intent.getStringExtra(ShoppingListActivity.EXTRA_PRODUCT_NAME));
            }
        } catch (Exception e) {}
        productDatabase = FirebaseDatabase.getInstance().getReference().child("Продукты");
        acceptCurrentBarCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                barCodeOutputTextView.setFocusable(false);
                cameraSource.release();
                barCodeCameraView.setVisibility(View.GONE);
                acceptCurrentBarCodeBtn.setEnabled(false);
            }
        });
        submitProductToDatabaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (productQuantityEditText.getText().toString().length() <1) {
                    productQuantityEditText.setText("0");
                }
                if (productManufacturer.getText().toString().length() < 1) {
                    productManufacturer.setText(" ");
                }
                if (productNameEditText.getText().length()>2) {
                    final Query checkProductRepeatQuery = productDatabase.orderByChild("name").equalTo(productNameEditText.getText().toString().substring(0,1).toUpperCase()+productNameEditText.getText().toString().substring(1).toLowerCase());
                    repetativityChecker = new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Product product = new Product(productNameEditText.getText().toString(),
                                    productCategories.getSelectedItem().toString(),
                                    productManufacturer.getText().toString(),
                                    Double.parseDouble(productQuantityEditText.getText().toString()),
                                    productQuantitiesTypes.getSelectedItem().toString());
                            for (DataSnapshot currentSnapshot : snapshot.getChildren()) {
                                Product databaseProduct = currentSnapshot.getValue(Product.class);
                                if (databaseProduct.getManufacturer().equals(product.getManufacturer())) {
                                    Toast.makeText(getApplicationContext(),"Товар с таким именем от такого производителя уже внесён в базу", Toast.LENGTH_LONG).show();
                                    return;
                                }
                            }
                            if (barCodeOutputTextView.getText().toString().length() > 0) {
                                product.addAssociatedBarCode(barCodeOutputTextView.getText().toString());
                            }
                            checkProductRepeatQuery.removeEventListener(repetativityChecker);
                            productDatabase.push().setValue(product).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(getApplicationContext(), "Продукт успешно внесён в базу", Toast.LENGTH_LONG).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(), "Не удалось внести продукт в базу", Toast.LENGTH_LONG).show();
                                }
                            });
                            finish();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    };
                    checkProductRepeatQuery.addValueEventListener(repetativityChecker);
                }
                else {
                    Toast.makeText(getApplicationContext(), "Имя продукта должно быть длиннее 2 символов", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected  void onPause() {
        super.onPause();
        if (barCodeCameraView.getVisibility()!=View.GONE) {
            cameraSource.release();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //initializeDetectorsAndSources();
    }

    private void bindViews() {
        barCodeOutputTextView = (EditText) findViewById(R.id.PrC_barCodeOutputView);
        barCodeCameraView = (SurfaceView) findViewById(R.id.PrC_BarCodeSurfaceView);
        acceptCurrentBarCodeBtn = (ImageButton) findViewById(R.id.PrC_acceptCurrentBarCode);
        submitProductToDatabaseBtn = (Button) findViewById(R.id.PrC_submitProductToDatabaseBtn);
        productCategories = (Spinner) findViewById(R.id.PrC_productCategorySpinner);
        productQuantitiesTypes = (Spinner) findViewById(R.id.PrC_quantityTypeSpinner);
        productNameEditText = (EditText) findViewById(R.id.PrC_setProductNameEditText);
        productQuantityEditText = (EditText) findViewById(R.id.PrC_productQuantityEditText);
        productManufacturer = (EditText) findViewById(R.id.PrC_manufacturerTextView);
        acceptCurrentBarCodeBtn.setEnabled(false);
    }

    private void setUpSpinners() {
        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(this, R.array.categories, android.R.layout.simple_spinner_dropdown_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        productCategories.setAdapter(categoryAdapter);
        productCategories.setOnItemSelectedListener(this);

        ArrayAdapter<CharSequence> typeOfQuantityAdapter = ArrayAdapter.createFromResource(this, R.array.typesOfQuantity, android.R.layout.simple_spinner_dropdown_item);
        typeOfQuantityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        productQuantitiesTypes.setAdapter(typeOfQuantityAdapter);
        productQuantitiesTypes.setOnItemSelectedListener(this);
    }

    private void initializeDetectorsAndSources() {
        barcodeDetector = new BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.ALL_FORMATS).build();
        cameraSource = new CameraSource.Builder(this, barcodeDetector).setRequestedPreviewSize(1920,1080).setAutoFocusEnabled(true).build();
        barCodeCameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(ProductConstructorActivity.this, Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED)
                    {
                        cameraSource.start(barCodeCameraView.getHolder());
                    }
                    else {
                        ActivityCompat.requestPermissions(ProductConstructorActivity.this, new String[] {Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
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
                    barCodeOutputTextView.post(new Runnable() {
                        @Override
                        public void run() {
                            if (barcodes.valueAt(0).email!=null) {
                                barCodeOutputTextView.removeCallbacks(null);
                                barCodeOutputTextView.setText(barcodes.valueAt(0).email.address);
                                acceptCurrentBarCodeBtn.setEnabled(true);
                            }
                            else {
                                if (!barcodes.valueAt(0).displayValue.equals(barCodeOutputTextView.getText().toString())) {
                                    Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                    v.vibrate(500);
                                }
                                barCodeOutputTextView.setText(barcodes.valueAt(0).displayValue);
                                acceptCurrentBarCodeBtn.setEnabled(true);
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}