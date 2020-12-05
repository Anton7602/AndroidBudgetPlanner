package com.example.budgetplanning;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;


//AnalyticsMainActivity currently represents an activity with
//calculations of ratio between nessecary food and unnessacary food
public class AnalyticsMainActivity extends AppCompatActivity {

    private TextView showPercentage, sumOfBadTransactionsTextView, sumOfGoodTransactionsTextView;
    private ProgressBar percentageCalculationPercentageBar;
    private RecyclerView showGoodFoodTransaction, showBadFoodTransaction;
    private DatabaseReference transactionDatabase;
    private RecyclerView.Adapter mBadAdapter, mGoodAdapter;
    private RecyclerView.LayoutManager mGoodLayoutManager, mBadLayoutManager;
    private ArrayList<Transaction> goodFoodList, badFoodList, tempList;
    private int currentYear, currentMonth, currentDay, maxDayInMonth, numberOfWeeksInMonth;
    private double sumOfGoodTransactions, sumOfBadTransactions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Initialazing Activity, setting up views and views parameters
        setContentView(R.layout.activity_analytics_main);
        bindViews();

        //Setting up date variables
        DateQueryHelper dateHelper = new DateQueryHelper();
        Calendar calendar = Calendar.getInstance();
        currentYear = calendar.get(Calendar.YEAR);
        currentMonth = calendar.get(Calendar.MONTH)+1;
        currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        Calendar newCalendar = new GregorianCalendar(currentYear, currentMonth, currentDay);
        maxDayInMonth = newCalendar.getActualMaximum(calendar.DAY_OF_MONTH);

        //Define query range, setting up databaseReference and constructing Query
        int startDate = dateHelper.dateParseToDatabaseDate(currentYear,currentMonth,1);
        int endDate = dateHelper.dateParseToDatabaseDate(currentYear,currentMonth, maxDayInMonth);
        transactionDatabase = FirebaseDatabase.getInstance().getReference().child("Транзакции");
        Query showTransactionsQuery = transactionDatabase.orderByChild("date").startAt(startDate).endAt(endDate);

        //Run query, sort transaction by nessecary food/unnessecary food categories
        showTransactionsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                goodFoodList = new ArrayList<>();
                badFoodList = new ArrayList<>();
                sumOfGoodTransactions = 0;
                sumOfBadTransactions = 0;
                for (DataSnapshot currentSnapshot : snapshot.getChildren()) {
                    Transaction transaction = currentSnapshot.getValue(Transaction.class);
                    if (transaction.getCategory().equals("Еда обязательная")) {
                        goodFoodList.add(transaction);
                        sumOfGoodTransactions = sumOfGoodTransactions+ transaction.getCost();
                    }
                    if (transaction.getCategory().equals("Еда необязательная")) {
                        badFoodList.add(transaction);
                        sumOfBadTransactions = sumOfBadTransactions + transaction.getCost();
                    }
                }
                //Set up gathered data on sums and ratio in corresponding TextViews
                sumOfGoodTransactionsTextView.setText(String.format("%,.2f",sumOfGoodTransactions)+" ₽");
                sumOfBadTransactionsTextView.setText(String.format("%,.2f", sumOfBadTransactions)+" ₽");
                percentageCalculationPercentageBar.setVisibility(ProgressBar.GONE);
                showPercentage.setText(String.format("%,.2f", (double) (sumOfGoodTransactions/(sumOfGoodTransactions+sumOfBadTransactions))*100)+" %");
                changeShapeColor((double) (sumOfGoodTransactions/(sumOfGoodTransactions+sumOfBadTransactions)));
                setUpRecyclerViews();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    //Binding views from layout to class variables
    private void bindViews() {
        showPercentage = (TextView) findViewById(R.id.showPercentage);
        sumOfBadTransactionsTextView = (TextView) findViewById(R.id.sumOfBadTransactionsTextView);
        sumOfGoodTransactionsTextView = (TextView) findViewById(R.id.sumOfGoodTransactionsTextView);
        showGoodFoodTransaction = (RecyclerView) findViewById(R.id.goodFoodRecyclerView);
        showBadFoodTransaction = (RecyclerView) findViewById(R.id.badFoodRecyclerView);
        percentageCalculationPercentageBar = (ProgressBar) findViewById(R.id.percentageCalculationBar);

    }

    //Defining Adapters, Data and LayoutManagers for RecyclerViews 
    private void setUpRecyclerViews() {
	    //set up parameters for good food Recycler View
            showBadFoodTransaction.setHasFixedSize(true);
            mBadAdapter = new TransactionHolderAdapter(separateTransactionsByWeeks(badFoodList));
            mBadLayoutManager = new LinearLayoutManager(AnalyticsMainActivity.this, LinearLayoutManager.HORIZONTAL, false);
            showBadFoodTransaction.setLayoutManager(mBadLayoutManager);
            showBadFoodTransaction.setAdapter(mBadAdapter);

            //set up parameters for bad food Recycler View
            showGoodFoodTransaction.setHasFixedSize(true);
            mGoodAdapter = new TransactionHolderAdapter(separateTransactionsByWeeks(goodFoodList));
            mGoodLayoutManager = new LinearLayoutManager(AnalyticsMainActivity.this,LinearLayoutManager.HORIZONTAL, false);
            showGoodFoodTransaction.setLayoutManager(mGoodLayoutManager);
            showGoodFoodTransaction.setAdapter(mGoodAdapter);
    }

    //Generate TransactionHolders with lists of Transactions separated by weeks
    private ArrayList<TransactionHolder> separateTransactionsByWeeks(ArrayList<Transaction> toSortTransactionArray) {
	    numberOfWeeksInMonth = 1;
        DateQueryHelper dateHelper = new DateQueryHelper();
	    ArrayList<TransactionHolder> transactionHolderList = new ArrayList<>();
	    while ((numberOfWeeksInMonth*7)-6<maxDayInMonth) {
            tempList = new ArrayList<Transaction>();
		    for (Transaction transaction : toSortTransactionArray) {
			    int transactionDay = Integer.parseInt(String.valueOf(transaction.getDate()).substring(6));
			    if (transactionDay>=((7*numberOfWeeksInMonth)-6) && transactionDay<7*numberOfWeeksInMonth) {
				    tempList.add(transaction);
			    }
		    }
		    TransactionHolder transactionHolder = new TransactionHolder(tempList,
		                                    dateHelper.dateParseToString(currentYear,currentMonth, (7*numberOfWeeksInMonth)-6) + "->" + dateHelper.dateParseToString(currentYear,currentMonth,Math.min(7*numberOfWeeksInMonth,maxDayInMonth)),
		                                                numberOfWeeksInMonth);
		    transactionHolderList.add(transactionHolder);
		    numberOfWeeksInMonth = numberOfWeeksInMonth+1;
	    }
	    return transactionHolderList;
    }

    private void changeShapeColor(double ratio) {
        if (ratio>0.6) {showPercentage.setBackground(getResources().getDrawable(R.drawable.ui_design_textview_circle_green)); }
        else {showPercentage.setBackground(getResources().getDrawable(R.drawable.ui_design_textview_circle_red)); }
    }
}