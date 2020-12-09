package com.example.budgetplanning;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

//QueryTransactionActivity is an Activity that allows user to generate 
//database query for transactions based on date range and selected category
public class QueryTransactionsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private TextView sumOfTransactionsView, dateTo, dateFrom;
    private RecyclerView mRecycleView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Spinner categoryFilterSpinner;
    private Button showData;
    private DatePickerDialog.OnDateSetListener setDateTo, setDateFrom;
    private DatabaseReference transactionDatabase;
    private ArrayList<Transaction> transactionsList;
    private double sumOfTransactions=0;
    private int currentDay, currentMonth, currentYear;
    private DateQueryHelper dateHelper;
    private ProgressBar loadingProgressBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
	    //Initializing Activity, setting up views, views parameters and databaseReference
        setContentView(R.layout.activity_query_transactions);
        bindViews();
	    setUpRecyclerViews();
	    setUpSpinners();
	    transactionDatabase = FirebaseDatabase.getInstance().getReference().child("Транзакции");

	    //Setting up date variables
        dateHelper = new DateQueryHelper();
        Calendar calendar = Calendar.getInstance();
        currentYear = calendar.get(Calendar.YEAR);
        currentMonth = calendar.get(Calendar.MONTH);
        currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        dateTo.setText(dateHelper.dateParseToString(currentYear, (currentMonth+1), currentDay));
        dateFrom.setText(dateHelper.dateParseToString(currentYear, (currentMonth+1), currentDay));
	
	//Setting OnClickListener on Right(Latest) date TextView to launch DatePicker
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

	//Setting OnClickListener on Left(Earliest) date TextView to launch DatePicker
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
	
	//Setting Listener on DatePickerDialog Closure (Picking Right(Latest) date)
        setDateTo = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                dateTo.setText(dateHelper.dateParseToString(year, (month+1), dayOfMonth));
                currentDay=dayOfMonth;
                currentMonth=month;
                currentYear=year;
            }
        };

	//Setting Listener on DatePickerDialog Closure (Picking Left(Earliest) date)
        setDateFrom = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                dateFrom.setText(dateHelper.dateParseToString(year, (month+1), dayOfMonth));
                currentDay=dayOfMonth;
                currentMonth=month;
                currentYear=year;
            }
        };

        showData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);
		        //Define query range (Acceptable if dates in textviews are switched)
                int startDate = dateHelper.dateParseToDatabaseDate(dateFrom.getText().toString());
                int endDate = dateHelper.dateParseToDatabaseDate(dateTo.getText().toString());
                if (startDate>endDate) {
                    dateTo.setText(dateHelper.dateParseToString(startDate));
                    dateFrom.setText(dateHelper.dateParseToString(endDate));
                    startDate = dateHelper.dateParseToDatabaseDate(dateFrom.getText().toString());
                    endDate = dateHelper.dateParseToDatabaseDate(dateTo.getText().toString());
                }

		        //Run query and filter incoming transaction with chosen category
                Query showTransactionsQuery = transactionDatabase.orderByChild("date").startAt(startDate).endAt(endDate);
                showTransactionsQuery.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        transactionsList.clear();
                        sumOfTransactions = 0;
                        for (DataSnapshot currentSnapshot : snapshot.getChildren()) {
                            Transaction transaction = currentSnapshot.getValue(Transaction.class);
                            if (!categoryFilterSpinner.getSelectedItem().toString().equals("Все"))
                            {
                                if (transaction.getCategory().equals(categoryFilterSpinner.getSelectedItem().toString()))
                                {
                                    transactionsList.add(transaction);
                                    sumOfTransactions=sumOfTransactions+transaction.getCost();
                                }
                            }
                            else {
                                transactionsList.add(transaction);
                                sumOfTransactions=sumOfTransactions+transaction.getCost();
                            }
                        }
                        loadingProgressBar.setVisibility(View.GONE);
                        mAdapter.notifyDataSetChanged();
                        sumOfTransactionsView.setText("Сумма транзакций: "+ String.format("%,.2f",sumOfTransactions) + " ₽");
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

    private void bindViews() {
        dateTo = (TextView) findViewById(R.id.QTr_dateFromBtn);
        dateFrom = (TextView) findViewById(R.id.QTr_dateToBtn);
        sumOfTransactionsView = (TextView) findViewById(R.id.QTr_sumOfTransactionsTextView);
        categoryFilterSpinner = (Spinner) findViewById(R.id.QTr_FilterCategorySpinner);
        mRecycleView=(RecyclerView) findViewById(R.id.QTr_showTransactionRecyclerView);
        showData = (Button) findViewById(R.id.QTr_showTransactionDataBtn);
        loadingProgressBar = (ProgressBar) findViewById(R.id.QTr_progressBar);
    }

    private void setUpRecyclerViews() {
	transactionsList = new ArrayList<Transaction>();
	mRecycleView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new TransactionAdapter(transactionsList);
        mRecycleView.setLayoutManager(mLayoutManager);
        mRecycleView.setAdapter(mAdapter);
    }

    private void setUpSpinners() {
	ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(this, R.array.filterCategories, android.R.layout.simple_spinner_dropdown_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoryFilterSpinner.setAdapter(categoryAdapter);
        categoryFilterSpinner.setOnItemSelectedListener(this);
    }
}