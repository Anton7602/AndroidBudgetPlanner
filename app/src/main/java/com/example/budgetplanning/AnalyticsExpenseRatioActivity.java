package com.example.budgetplanning;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.razerdp.widget.animatedpieview.AnimatedPieView;
import com.razerdp.widget.animatedpieview.AnimatedPieViewConfig;
import com.razerdp.widget.animatedpieview.BasePieLegendsView;
import com.razerdp.widget.animatedpieview.DefaultCirclePieLegendsView;
import com.razerdp.widget.animatedpieview.DefaultPieLegendsView;
import com.razerdp.widget.animatedpieview.callback.OnPieLegendBindListener;
import com.razerdp.widget.animatedpieview.callback.OnPieSelectListener;
import com.razerdp.widget.animatedpieview.data.IPieInfo;
import com.razerdp.widget.animatedpieview.data.SimplePieInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Random;


public class AnalyticsExpenseRatioActivity extends AppCompatActivity {

    private AnimatedPieView expensesRatePieChart;
    private AnimatedPieViewConfig expensesRatePieChartConfig;
    private ProgressBar pieChartDataLoadingProgressBar;
    private TextView pieChartInfo;
    private NumberPicker yearPicker, monthPicker;
    private DatabaseReference transactionDatabase;
    private int currentYear, currentMonth, currentDay;
    private ArrayList<Float> categoriesValuesArray;
    private Double pieChartSum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Initialazing Activity, setting up views and views parameters
        setContentView(R.layout.activity_analytics_expense_ratio);
        bindViews();

        //Setting up date variables
        DateQueryHelper dateHelper = new DateQueryHelper();
        Calendar calendar = Calendar.getInstance();
        currentYear = calendar.get(Calendar.YEAR);
        currentMonth = calendar.get(Calendar.MONTH)+1;
        currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        datePickerSetup();

        final ArrayList<String> categoriesArray = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.categories)));
        categoriesValuesArray = new ArrayList<Float>();
        for (int i =0; i<categoriesArray.size();i++ ) {
            categoriesValuesArray.add(0.0f);
        }
        int startDate = dateHelper.dateParseToDatabaseDate(currentYear,currentMonth,1);
        int endDate = dateHelper.dateParseToDatabaseDate(currentYear,currentMonth, currentDay);

        transactionDatabase = FirebaseDatabase.getInstance().getReference().child("Транзакции");
        Query showTransactionsQuery = transactionDatabase.orderByChild("date").startAt(startDate).endAt(endDate);

        //Run query, sort transaction by nessecary food/unnessecary food categories
        showTransactionsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                expensesRatePieChartConfig = new AnimatedPieViewConfig();
                pieChartSum=0.0;
                for (DataSnapshot currentSnapshot : snapshot.getChildren()) {
                    Transaction transaction = currentSnapshot.getValue(Transaction.class);
                    categoriesValuesArray.set(categoriesArray.indexOf(transaction.getCategory()), categoriesValuesArray.get(categoriesArray.indexOf(transaction.getCategory()))+(float) transaction.getCost());
                }
                Random colorRandomizer = new Random();
                for (String category : categoriesArray) {
                    if (categoriesValuesArray.get(categoriesArray.indexOf(category))>0) {
                        expensesRatePieChartConfig.addData(new SimplePieInfo(categoriesValuesArray.get(categoriesArray.indexOf(category)),
                                                                            Color.rgb(colorRandomizer.nextInt(254),colorRandomizer.nextInt(254),colorRandomizer.nextInt(254)),
                                                                            category));
                        pieChartSum=pieChartSum+categoriesValuesArray.get(categoriesArray.indexOf(category));
                    }
                }
                setUpPieChartConfig();
                pieChartDataLoadingProgressBar.setVisibility(View.GONE);
                pieChartInfo.setText("100%"+ System.lineSeparator()+String.format("%,.2f", pieChartSum)+"₽");
                expensesRatePieChart.start(expensesRatePieChartConfig);
                pieChartInfo.setVisibility(View.VISIBLE);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void bindViews() {
        expensesRatePieChart = findViewById(R.id.AER_ExpensesRatioPieChart);
        pieChartDataLoadingProgressBar = findViewById(R.id.AER_pieChartDataLoadingProgressBar);
        pieChartInfo = (TextView) findViewById(R.id.AER_pieChartInfoTextView);
        yearPicker = (NumberPicker) findViewById(R.id.AER_yearPicker);
        monthPicker = (NumberPicker) findViewById(R.id.AER_monthPicker);
    }

    private void datePickerSetup() {
        yearPicker.setMinValue(2020);
        yearPicker.setMaxValue(currentYear);
        yearPicker.setValue(currentYear);
        String[] months = getResources().getStringArray(R.array.months);
        monthPicker.setMinValue(0);
        monthPicker.setMaxValue(months.length-1);
        monthPicker.setDisplayedValues(months);
    }

    private void setUpPieChartConfig () {
        expensesRatePieChartConfig.startAngle(90);
        expensesRatePieChartConfig.duration(2000);
        expensesRatePieChartConfig.textSize(20);
        expensesRatePieChartConfig.drawText(true);
        expensesRatePieChartConfig.selectListener(new OnPieSelectListener<IPieInfo>() {
            @Override
            public void onSelectPie(@NonNull IPieInfo pieInfo, boolean isFloatUp) {
                if (isFloatUp) {
                    Double percentage = (pieInfo.getValue() / pieChartSum) * 100;
                    pieChartInfo.setText(String.format("%,.2f", percentage)+"%"+System.lineSeparator()+String.format("%,.2f", pieInfo.getValue())+"₽");
                    pieChartInfo.setTextColor(pieInfo.getColor());
                }
                else {
                    pieChartInfo.setTextColor(Color.BLACK);
                    pieChartInfo.setText("100%"+ System.lineSeparator()+String.format("%,.2f", pieChartSum)+"₽");
                }
            }
        });
    }
}