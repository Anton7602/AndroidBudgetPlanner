package com.example.budgetplanning;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

public class TransactionEditActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, CompoundButton.OnCheckedChangeListener {
    final static public String EXTRA_PRODUCT_NAME = "ProductName";

    private Spinner category, typeOfQuantity, typeOfCurrency;
    private TextView editDate, QuantityTextView;
    private DatePickerDialog.OnDateSetListener mOnDateSetListener;
    private Button submitTransaction, deleteTransaction;
    private ImageButton toProductConstructor;
    private DatabaseReference mDatabase, productDatabase;
    private EditText editCost, editQuantity;
    private AutoCompleteTextView editName;
    private SwitchCompat isProductSwitch;
    private int currentYear, currentMonth, currentDay;
    private DateQueryHelper dateHelper;
    private Boolean isProduct = true;
    private ArrayList<Product> productList = new ArrayList<>();
    private ArrayList<String> productNamesList = new ArrayList<>();
    private ArrayList<String> productKeys = new ArrayList<>();
    private String transactionKey, quantityType, Category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_edit);
        bindViews();
        setUpSpinners();
        editName.requestFocus();
        productDatabase = FirebaseDatabase.getInstance().getReference().child("Продукты");
        productDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot currentSnapshot : snapshot.getChildren()) {
                    Product product = currentSnapshot.getValue(Product.class);
                    productList.add(product);
                    productNamesList.add(product.getName() +" " + product.getManufacturer());
                    productKeys.add(currentSnapshot.getKey());
                    setUpAutocompleteTextViews();
                    editName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            ArrayList<String> categoriesArray = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.categories)));
                            ArrayList<String> typesOfQuantityArray =new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.typesOfQuantity)));
                            for (Product product : productList) {
                                if (parent.getItemAtPosition(position).equals(product.getName()+" "+ product.getManufacturer())) {
                                    category.setSelection(categoriesArray.indexOf(product.getDefaultCategory()));
                                    typeOfQuantity.setSelection(typesOfQuantityArray.indexOf(product.getDefaultQuantityType()));
                                    editQuantity.requestFocus();
                                    if (product.getDefaultQuantity()!=0) {
                                        editQuantity.setText(String.valueOf(product.getDefaultQuantity()));
                                        editCost.requestFocus();
                                    }
                                    else {
                                        editQuantity.setText("");
                                    }
                                }
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        final Intent intent = getIntent();
        try
        {
            if (intent.getStringExtra(TransactionAdapter.TransactionViewHolder.EXTRA_PRODUCT_NAME).length()>1)
            {
                editName.setText(intent.getStringExtra(TransactionAdapter.TransactionViewHolder.EXTRA_PRODUCT_NAME));
            }
            if (intent.getStringExtra(TransactionAdapter.TransactionViewHolder.EXTRA_PRODUCT_COST).length()>1)
            {
                editCost.setText(intent.getStringExtra(TransactionAdapter.TransactionViewHolder.EXTRA_PRODUCT_COST));
            }
            if (intent.getStringExtra(TransactionAdapter.TransactionViewHolder.EXTRA_PRODUCT_QUANTITY).length()>1)
            {
                editQuantity.setText(intent.getStringExtra(TransactionAdapter.TransactionViewHolder.EXTRA_PRODUCT_QUANTITY));
            }
            else
            {
                isProduct=false;
            }
            if (intent.getStringExtra(TransactionAdapter.TransactionViewHolder.EXTRA_PRODUCT_QUANTITY).length()>1)
            {
                editQuantity.setText(intent.getStringExtra(TransactionAdapter.TransactionViewHolder.EXTRA_PRODUCT_QUANTITY));
            }
            if (intent.getStringExtra(TransactionAdapter.TransactionViewHolder.EXTRA_PRODUCT_QUANTITY).length()>1)
            {
                editQuantity.setText(intent.getStringExtra(TransactionAdapter.TransactionViewHolder.EXTRA_PRODUCT_QUANTITY));
            }
            if (intent.getStringExtra(TransactionAdapter.TransactionViewHolder.EXTRA_PRODUCT_KEY).length()>1)
            {
                transactionKey = intent.getStringExtra(TransactionAdapter.TransactionViewHolder.EXTRA_PRODUCT_KEY);
            }

        } catch (Exception e) {}


        Calendar calendar = Calendar.getInstance();
        dateHelper = new DateQueryHelper();
        currentYear = calendar.get(Calendar.YEAR);
        currentMonth = calendar.get(Calendar.MONTH);
        currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        editDate.setText(dateHelper.dateParseToString(currentYear, (currentMonth+1), currentDay));
        editDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = new DatePickerDialog(TransactionEditActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog,
                        mOnDateSetListener,
                        currentYear,currentMonth,currentDay);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mOnDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                editDate.setText(dateHelper.dateParseToString(year, (month+1), dayOfMonth));
                currentDay=dayOfMonth;
                currentMonth=month;
                currentYear=year;
            }
        };

        isProductSwitch.setOnCheckedChangeListener(this);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Транзакции");

        deleteTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase.child(transactionKey).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            //Toast.makeText(getApplicationContext(), "I'm about to delete a" + transactionKey, Toast.LENGTH_LONG).show();
                            mDatabase.child(transactionKey).removeValue();
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });


        toProductConstructor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openProductConstructor();
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void bindViews() {
        category = (Spinner) findViewById(R.id.TrE_categorySpinner);
        typeOfQuantity = (Spinner) findViewById(R.id.TrE_typeOfQuantitySpinner);
        typeOfCurrency = (Spinner) findViewById(R.id.TrE_typeOfCurrencySpinner);
        editName=(AutoCompleteTextView) findViewById(R.id.TrE_productName);
        editCost=(EditText) findViewById(R.id.TrE_productCost);
        editQuantity=(EditText) findViewById(R.id.TrE_productQuantity);
        editDate = (TextView) findViewById(R.id.TrE_transactionDate);
        submitTransaction = (Button) findViewById(R.id.TrE_submitTransaction);
        isProductSwitch = (SwitchCompat) findViewById(R.id.TrE_isProductSwitch);
        QuantityTextView = (TextView) findViewById(R.id.TrE_quantityTextView);
        toProductConstructor = (ImageButton) findViewById(R.id.TrE_toProductConstructorBtn);
        deleteTransaction = (Button) findViewById((R.id.TrE_deleteTransaction));
    }

    private void openProductConstructor() {
        Intent openProductConstructor = new Intent(this, ProductConstructorActivity.class);
        openProductConstructor.putExtra(EXTRA_PRODUCT_NAME, editName.getText().toString());
        startActivity(openProductConstructor);
    }

    private void setUpAutocompleteTextViews () {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, productNamesList);
        editName.setAdapter(adapter);
    }

    private void setUpSpinners() {
        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(this, R.array.categories, android.R.layout.simple_spinner_dropdown_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category.setAdapter(categoryAdapter);
        category.setOnItemSelectedListener(this);

        ArrayAdapter<CharSequence> typeOfQuantityAdapter = ArrayAdapter.createFromResource(this, R.array.typesOfQuantity, android.R.layout.simple_spinner_dropdown_item);
        typeOfQuantity.setAdapter(typeOfQuantityAdapter);
        typeOfQuantity.setOnItemSelectedListener(this);


        ArrayAdapter<CharSequence> typeOfCurrencyAdapter = ArrayAdapter.createFromResource(this, R.array.typesOfCurrency, android.R.layout.simple_spinner_dropdown_item);
        typeOfCurrency.setAdapter(typeOfCurrencyAdapter);
        typeOfCurrency.setOnItemSelectedListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isProduct) {
            isProduct = false;
            isProductSwitch.setText("Услуга");
            editName.setHint("Введите имя услуги");
            QuantityTextView.setVisibility(View.GONE);
            editQuantity.setVisibility(View.GONE);
            typeOfQuantity.setVisibility(View.GONE);
            toProductConstructor.setVisibility(View.GONE);
        }
        else {
            isProduct = true;
            isProductSwitch.setText("Продукт");
            editName.setHint("Введите имя продукта");
            QuantityTextView.setVisibility(View.VISIBLE);
            editQuantity.setVisibility(View.VISIBLE);
            typeOfQuantity.setVisibility(View.VISIBLE);
            toProductConstructor.setVisibility(View.VISIBLE);
        }
    }
}