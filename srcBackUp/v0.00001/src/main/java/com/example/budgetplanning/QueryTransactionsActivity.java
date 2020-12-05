package com.example.budgetplanning;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class QueryTransactionsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private TextView sumOfTransactionsView;
    private TextView dateTo;
    private TextView dateFrom;
    private RecyclerView mRecycleView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Spinner categoryFilterSpinner;
    private DatePickerDialog.OnDateSetListener setDateTo;
    private DatePickerDialog.OnDateSetListener setDateFrom;
    private DatabaseReference transactionDatabase;
    private ArrayList<Transaction> transactionsList = new ArrayList<>();
    double sumOfTransactions;
    int currentDay;
    int currentMonth;
    int currentYear;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException ignored){}
        setContentView(R.layout.activity_query_transactions);

        Calendar calendar = Calendar.getInstance();
        currentYear = calendar.get(Calendar.YEAR);
        currentMonth = calendar.get(Calendar.MONTH);
        currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        dateTo = (TextView) findViewById(R.id.dateFromBtn);
        dateFrom = (TextView) findViewById(R.id.dateToBtn);
        dateTo.setText(currentDay+"/"+(currentMonth+1)+"/"+currentYear);
        dateFrom.setText(currentDay+"/"+(currentMonth+1)+"/"+currentYear);
        if ((currentMonth+1)<10)
        {
            dateTo.setText(currentDay+"/0"+(currentMonth+1)+"/"+currentYear);
            dateFrom.setText(currentDay+"/0"+(currentMonth+1)+"/"+currentYear);
        }
        if (currentDay<10)
        {
            dateTo.setText("0"+dateTo.getText());
            dateFrom.setText("0"+dateFrom.getText());
        }
        dateTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = new DatePickerDialog(QueryTransactionsActivity.this,
                        android.R.style.Theme_Holo_Dialog_NoActionBar_MinWidth,
                        setDateTo,
                        currentYear,currentMonth,currentDay);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
        dateFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = new DatePickerDialog(QueryTransactionsActivity.this,
                        android.R.style.Theme_Holo_Dialog_NoActionBar_MinWidth,
                        setDateFrom,
                        currentYear,currentMonth,currentDay);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
        setDateTo = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String date= dayOfMonth + "/" + (month+1) + "/" + year;
                if ((month+1)<10) {
                    date = dayOfMonth + "/0" + (month+1) + "/" + year;
                }
                if (dayOfMonth<10){
                    date = "0"+date;
                }
                dateTo.setText(date);
                currentDay=dayOfMonth;
                currentMonth=month;
                currentYear=year;
            }
        };
        setDateFrom = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String date= dayOfMonth + "/" + (month+1) + "/" + year;
                if ((month+1)<10) {
                    date = dayOfMonth + "/0" + (month+1) + "/" + year;
                }
                if (dayOfMonth<10){
                    date = "0"+date;
                }
                dateFrom.setText(date);
                currentDay=dayOfMonth;
                currentMonth=month;
                currentYear=year;
            }
        };


        categoryFilterSpinner = (Spinner) findViewById(R.id.FilterCategorySpinner);
        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(this, R.array.filterCategories, android.R.layout.simple_spinner_dropdown_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoryFilterSpinner.setAdapter(categoryAdapter);
        categoryFilterSpinner.setOnItemSelectedListener(this);

        sumOfTransactionsView = (TextView) findViewById(R.id.sumOfTransactionsTextView);
        transactionDatabase = FirebaseDatabase.getInstance().getReference().child("Транзакции");
        mRecycleView=findViewById(R.id.showTransactionRecyclerView);
        mRecycleView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new TransactionAdapter(transactionsList);
        mRecycleView.setLayoutManager(mLayoutManager);
        mRecycleView.setAdapter(mAdapter);

        Button showData = (Button) findViewById(R.id.showTransactionDataBtn);
        showData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int startDate = Integer.parseInt(dateFrom.getText().toString().substring(6)+
                        dateFrom.getText().toString().substring(3,5)+
                        dateFrom.getText().toString().substring(0,2));
                int endDate = Integer.parseInt(dateTo.getText().toString().substring(6)+
                        dateTo.getText().toString().substring(3,5)+
                        dateTo.getText().toString().substring(0,2));
                Query showTransactionsQuery = transactionDatabase.orderByChild("date").startAt(startDate).endAt(endDate);
                showTransactionsQuery.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        transactionsList.clear();
                        ArrayList<Transaction> tempTransactionList = new ArrayList<>();
                        for (DataSnapshot currentSnapshot : snapshot.getChildren()) {
                            Transaction transaction = currentSnapshot.getValue(Transaction.class);
                            transactionsList.add(transaction);
                        }
                        if (!categoryFilterSpinner.getSelectedItem().toString().equals("Все"))
                        {
                            for (Transaction transaction : transactionsList){
                                if (transaction.getCategory().equals(categoryFilterSpinner.getSelectedItem().toString()))
                                {
                                    tempTransactionList.add(transaction);
                                }
                            }
                            transactionsList.clear();
                            for (Transaction transaction : tempTransactionList){
                                transactionsList.add(transaction);
                            }
                        }
                        mAdapter.notifyDataSetChanged();
                        sumOfTransactions=0;
                        for (Transaction transaction : transactionsList) {
                            sumOfTransactions=sumOfTransactions+transaction.getCost();
                        }
                        sumOfTransactions=(double) Math.round(sumOfTransactions*100)/100;
                        sumOfTransactionsView.setText("Сумма транзакций: "+ sumOfTransactions + " ₽");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
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