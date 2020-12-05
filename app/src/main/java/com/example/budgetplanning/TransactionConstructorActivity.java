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
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class TransactionConstructorActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, CompoundButton.OnCheckedChangeListener {

    private Spinner category, typeOfQuantity, typeOfCurrency;
    private TextView editDate, QuantityTextView;
    private DatePickerDialog.OnDateSetListener mOnDateSetListener;
    private Button submitTransaction;
    private DatabaseReference mDatabase;
    private EditText editName, editCost, editQuantity;
    private SwitchCompat isProductSwitch;
    private int currentYear, currentMonth, currentDay;
    private DateQueryHelper dateHelper;
    private Boolean isProduct = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_constructor);
        bindViews();
	    setUpSpinners();

        final Intent intent = getIntent();
        try
        {
            if (intent.getStringExtra(ShoppingListActivity.EXTRA_PRODUCT_NAME).length()>1)
            {
                editName.setText(intent.getStringExtra(ShoppingListActivity.EXTRA_PRODUCT_NAME));
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
                DatePickerDialog dialog = new DatePickerDialog(TransactionConstructorActivity.this,
                        android.R.style.Theme_Holo_Dialog_NoActionBar_MinWidth,
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
        submitTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editName.getText().toString().length()> 2 && !editCost.getText().toString().matches("")) {
                    Transaction transaction;
                    try {
                        if (isProduct) {
                            if (!editQuantity.getText().toString().matches("")) {
                                transaction = new Transaction(category.getSelectedItem().toString(),
                                        editName.getText().toString().toUpperCase().substring(0, 1) + editName.getText().toString().trim().substring(1),
                                        typeOfQuantity.getSelectedItem().toString(),
                                        Double.parseDouble(editQuantity.getText().toString()),
                                        typeOfCurrency.getSelectedItem().toString(),
                                        Double.parseDouble(editCost.getText().toString()),
                                        dateHelper.dateParseToDatabaseDate(editDate.getText().toString()),
                                        isProduct);
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(), "Некорректно введено количество товара", Toast.LENGTH_LONG).show();
                                return;
                            }
                        } else {
                            transaction = new Transaction(category.getSelectedItem().toString(),
                                    editName.getText().toString().toUpperCase().substring(0, 1) + editName.getText().toString().trim().substring(1),
                                    typeOfCurrency.getSelectedItem().toString(),
                                    Double.parseDouble(editCost.getText().toString()),
                                    dateHelper.dateParseToDatabaseDate(editDate.getText().toString()),
                                    !isProduct);
                        }
                        mDatabase.push().setValue(transaction).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getApplicationContext(), "Транзакция успешно внесена базу", Toast.LENGTH_LONG).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), "Не удалось внести транзакицю в базу", Toast.LENGTH_LONG);
                            }
                        });
                        editName.setText("");
                        editCost.setText("");
                        editQuantity.setText("");
                    } catch (Exception ex) {
                        Toast.makeText(getApplicationContext(), ex.getMessage().toString(), Toast.LENGTH_LONG).show();
                        return;
                    }
                    try {
                        if (intent.getStringExtra(ShoppingListActivity.EXTRA_PRODUCT_NAME).length() > 1) {
                            finish();
                        }
                    } catch (Exception e) {
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Наименование расхода должно быть длинее 2 символов, а стоимость больше нуля!!", Toast.LENGTH_LONG).show();
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

    private void bindViews() {
        category = (Spinner) findViewById(R.id.categorySpinner);
        typeOfQuantity = (Spinner) findViewById(R.id.typeOfQuantitySpinner);
        typeOfCurrency = (Spinner) findViewById(R.id.typeOfCurrencySpinner);
        editName=(EditText) findViewById(R.id.productName);
        editCost=(EditText) findViewById(R.id.productCost);
        editQuantity=(EditText) findViewById(R.id.productQuantity);
        editDate = (TextView) findViewById(R.id.transactionDate);
        submitTransaction = (Button) findViewById(R.id.submitTransaction);
        isProductSwitch = (SwitchCompat) findViewById(R.id.isProductSwitch);
        QuantityTextView = (TextView) findViewById(R.id.quantityTextView);

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
        isProduct = (!isProduct);
        if (isProduct) {
            isProductSwitch.setText("Продукт");
            editName.setHint("Введите имя продукта");
            QuantityTextView.setVisibility(View.VISIBLE);
            editQuantity.setVisibility(View.VISIBLE);
            typeOfQuantity.setVisibility(View.VISIBLE);
        }
        else {
            isProductSwitch.setText("Услуга");
            editName.setHint("Введите имя услуги");
            QuantityTextView.setVisibility(View.GONE);
            editQuantity.setVisibility(View.GONE);
            typeOfQuantity.setVisibility(View.GONE);
        }
    }
}