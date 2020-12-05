package com.example.budgetplanning;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class TransactionConstructorActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Spinner category;
    private Spinner typeOfQuantity;
    private Spinner typeOfCurrency;

    private TextView editDate;
    private DatePickerDialog.OnDateSetListener mOnDateSetListener;
    private Button submitTransaction;
    private DatabaseReference mDatabase;
    private EditText editName, editCost, editQuantity;

    int currentYear;
    int currentMonth;
    int currentDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException ignored){}
        setContentView(R.layout.activity_transaction_constructor);


        Calendar calendar = Calendar.getInstance();
        currentYear = calendar.get(Calendar.YEAR);
        currentMonth = calendar.get(Calendar.MONTH);
        currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        category = (Spinner) findViewById(R.id.categorySpinner);
        typeOfQuantity = (Spinner) findViewById(R.id.typeOfQuantitySpinner);
        typeOfCurrency = (Spinner) findViewById(R.id.typeOfCurrencySpinner);
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

        editName=(EditText) findViewById(R.id.productName);
        editCost=(EditText) findViewById(R.id.productCost);
        editQuantity=(EditText) findViewById(R.id.productQuantity);

        final Intent intent = getIntent();
        try
        {
            if (intent.getStringExtra(ShoppingListActivity.EXTRA_PRODUCT_NAME).length()>1)
            {
                editName.setText(intent.getStringExtra(ShoppingListActivity.EXTRA_PRODUCT_NAME));
            }
        } catch (Exception e) {}
        editDate = (TextView) findViewById(R.id.transactionDate);
        editDate.setText(currentDay+"/"+(currentMonth+1)+"/"+currentYear);
        if ((currentMonth+1)<10)
        {
            editDate.setText(currentDay+"/0"+(currentMonth+1)+"/"+currentYear);
        }
        if (currentDay<10)
        {
            editDate.setText("0"+editDate.getText());
        }
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
                String date= dayOfMonth + "/" + (month+1) + "/" + year;
                if ((month+1)<10) {
                    date = dayOfMonth + "/0" + (month+1) + "/" + year;
                }
                if (dayOfMonth<10){
                    date = "0"+date;
                }
                editDate.setText(date);
                currentDay=dayOfMonth;
                currentMonth=month;
                currentYear=year;
            }
        };
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Транзакции");
        submitTransaction = (Button) findViewById(R.id.submitTransaction);
        submitTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editName.getText().toString().length()>2 &&
                        Double.parseDouble(editQuantity.getText().toString())>0 &&
                        Double.parseDouble(editCost.getText().toString())>0) {
                String setCategory = category.getSelectedItem().toString();
                String setName = editName.getText().toString().toUpperCase().substring(0, 1) + editName.getText().toString().trim().substring(1);
                String setTypeofQuantity = typeOfQuantity.getSelectedItem().toString();
                double setQuantity = Double.parseDouble(editQuantity.getText().toString());
                String setTypeofCurrency = typeOfCurrency.getSelectedItem().toString();
                double setCost = Double.parseDouble(editCost.getText().toString());
                String setDay = editDate.getText().toString().substring(0,2);
                String setMonth = editDate.getText().toString().substring(3,5);
                String setYear = editDate.getText().toString().substring(6);
                int setDate = Integer.parseInt(setYear+setMonth+setDay);
                    Transaction transaction = new Transaction(setCategory, setName, setTypeofQuantity, setQuantity, setTypeofCurrency, setCost, setDate);
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
                    try {
                        if (intent.getStringExtra(ShoppingListActivity.EXTRA_PRODUCT_NAME).length()>1) {
                            finish();
                        }
                    } catch (Exception e) {}
                }
                else {
                    Toast.makeText(getApplicationContext(), "Введённые данные некорректны", Toast.LENGTH_LONG).show();
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