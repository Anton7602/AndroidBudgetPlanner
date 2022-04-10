package com.example.budgetplanning.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.budgetplanning.R;

public class AnalyticsMainActivity extends AppCompatActivity {

    Button toFoodBalanceButton, toExpenseRatioButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics_main);
        bindViews();

        toFoodBalanceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFoodBalance();
            }
        });

        toExpenseRatioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openExpenseRatio();
            }
        });
    }

    private void bindViews() {
        toFoodBalanceButton = findViewById(R.id.AM_AnalyticsToFoodBalanceBtn);
        toExpenseRatioButton = findViewById(R.id.AM_AnalyticsToExpenseRatioBtn);
    }

    private void openFoodBalance() {
        Intent intent = new Intent(this, AnalyticsFoodBalanceActivity.class);
        startActivity(intent);
    }

    private void openExpenseRatio() {
        Intent intent = new Intent(this, AnalyticsExpenseRatioActivity.class);
        startActivity(intent);
    }
}