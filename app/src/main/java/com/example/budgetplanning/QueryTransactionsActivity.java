package com.example.budgetplanning;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

//QueryTransactionActivity is an Activity that allows user to generate 
//database query for transactions based on date range and selected category
public class QueryTransactionsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private TextView sumOfTransactionsView, dateTo, dateFrom, quantityOfProductsInTransactions;
    private EditText keyWordsFilter;
    private RecyclerView mRecycleView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Spinner categoryFilterSpinner;
    private Button showData;
    private DatePickerDialog.OnDateSetListener setDateTo, setDateFrom;
    private DatabaseReference transactionDatabase;
    private ArrayList<Transaction> transactionsList;
    private double sumOfTransactions=0, quantityOfTransactions=0;
    private int currentDay, currentMonth, currentYear;
    private DateQueryHelper dateHelper;
    private ProgressBar loadingProgressBar;
    private ImageButton changeSumInfo;
    private FirebaseAuth mAuth;



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
                        android.R.style.Theme_Holo_Light_Dialog,
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
                        android.R.style.Theme_Holo_Light_Dialog,
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
                            if (matchesAllFilters(transaction)) {
                                transactionsList.add(transaction);
                                sumOfTransactions=sumOfTransactions+transaction.getCost();
                            }
                        }
                        UpdateQuantityInfo();
                        mAdapter.notifyDataSetChanged();
                        loadingProgressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        changeSumInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sumOfTransactionsView.getText().toString().contains("Сумма")) {
                    sumOfTransactionsView.setText("Средняя цена: " + String.format("%,.2f",sumOfTransactions/quantityOfTransactions) + " ₽/"+transactionsList.get(0).getTypeOfQuantity());
                }
                else {
                    sumOfTransactionsView.setText("Сумма транзакций: "+ String.format("%,.2f",sumOfTransactions) + " ₽");
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

    private boolean KeywordsPresent (String keyWords, String productName) {
       String[] separatedKeyWords = keyWords.split(" ");
       for (String separatedKeyWord : separatedKeyWords) {
           if (!productName.toLowerCase().contains(separatedKeyWord.toLowerCase())) {
               return false;
           }
       }
       return true;
    }


    private boolean QuantityTypesMatch (ArrayList<Transaction> listOfTransactions) {
        String currentQuantityType = " ";
        for (Transaction transaction : listOfTransactions) {
            if (currentQuantityType.equals(" ")) {
                currentQuantityType = transaction.getTypeOfQuantity();
            }
            else {
                if (!currentQuantityType.equals(transaction.getTypeOfQuantity())) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean matchesAllFilters(Transaction transaction) {
        //checking category filter
        if (!categoryFilterSpinner.getSelectedItem().toString().equals("Все"))
        {
            if (!transaction.getCategory().equals(categoryFilterSpinner.getSelectedItem().toString()))
            {
                return false;
            }
        }
        //checking keywords filter
        if (keyWordsFilter.getText().toString().length()>0) {
            if (!KeywordsPresent(keyWordsFilter.getText().toString(), transaction.getName())) {
                return false;
            }
        }
        return true;
    }

    private void UpdateQuantityInfo() {
        if (transactionsList.size()>0) {
            if (QuantityTypesMatch(transactionsList) && transactionsList.get(0).getTypeOfQuantity().length()>0) {
                quantityOfTransactions = 0;
                for (Transaction transaction : transactionsList) {
                    quantityOfTransactions += transaction.getQuantity();
                }
                quantityOfProductsInTransactions.setText(String.format("%,.2f", quantityOfTransactions) + " "+ transactionsList.get(0).getTypeOfQuantity());
                changeSumInfo.setVisibility(View.VISIBLE);
                if (sumOfTransactionsView.getText().toString().contains("Средняя")) {
                    sumOfTransactionsView.setText("Средняя цена: " + String.format("%,.2f",sumOfTransactions/quantityOfTransactions) + " ₽/"+transactionsList.get(0).getTypeOfQuantity());
                }
                else {
                    sumOfTransactionsView.setText("Сумма транзакций: "+ String.format("%,.2f",sumOfTransactions) + " ₽");
                }
            }
            else  {
                quantityOfProductsInTransactions.setText(transactionsList.size() + " тр.");
                sumOfTransactionsView.setText("Сумма транзакций: "+ String.format("%,.2f",sumOfTransactions) + " ₽");
                changeSumInfo.setVisibility(View.GONE);
            }
            quantityOfProductsInTransactions.setVisibility(View.VISIBLE);
            sumOfTransactionsView.setVisibility(View.VISIBLE);
        }
        else {
            sumOfTransactionsView.setVisibility(View.GONE);
            quantityOfProductsInTransactions.setVisibility(View.GONE);
            changeSumInfo.setVisibility(View.GONE);
        }
    }

    private void bindViews() {
        dateTo = (TextView) findViewById(R.id.QTr_dateFromBtn);
        dateFrom = (TextView) findViewById(R.id.QTr_dateToBtn);
        sumOfTransactionsView = (TextView) findViewById(R.id.QTr_sumOfTransactionsTextView);
        categoryFilterSpinner = (Spinner) findViewById(R.id.QTr_FilterCategorySpinner);
        mRecycleView=(RecyclerView) findViewById(R.id.QTr_showTransactionRecyclerView);
        showData = (Button) findViewById(R.id.QTr_showTransactionDataBtn);
        loadingProgressBar = (ProgressBar) findViewById(R.id.QTr_progressBar);
        quantityOfProductsInTransactions = (TextView) findViewById(R.id.QTr_QuantityTextView);
        keyWordsFilter = (EditText) findViewById(R.id.QTr_FilterEditText);
        changeSumInfo = (ImageButton) findViewById(R.id.QTr_ChangeSumInfoBtn);
    }

    private void setUpRecyclerViews() {
	transactionsList = new ArrayList<Transaction>();
	mRecycleView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new TransactionAdapter(transactionsList);
        mRecycleView.setLayoutManager(mLayoutManager);
        new ItemTouchHelper(recyclerViewSwiper).attachToRecyclerView(mRecycleView);
        mRecycleView.setAdapter(mAdapter);
    }

    private void setUpSpinners() {
	ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(this, R.array.filterCategories, android.R.layout.simple_spinner_dropdown_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoryFilterSpinner.setAdapter(categoryAdapter);
        categoryFilterSpinner.setOnItemSelectedListener(this);
    }

    ItemTouchHelper.SimpleCallback recyclerViewSwiper = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            sumOfTransactions -= transactionsList.get(viewHolder.getAdapterPosition()).getCost();
            transactionsList.remove(viewHolder.getAdapterPosition());
            UpdateQuantityInfo();
            mAdapter.notifyDataSetChanged();
        }
    };
}