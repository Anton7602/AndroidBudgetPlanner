package com.example.budgetplanning;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


//Main activity is a navigation main menu to the rest of th application


public class MainActivity extends AppCompatActivity {

    Button toShoppingList, toAddTransaction, toShowTransactions, toAnalytics ,toFinancialAssets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindViews();

        toShoppingList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openShoppingList();
            }
        });

        toAddTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddTransaction();
            }
        });

        toShowTransactions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openShowTransactions();
            }
        });

        toAnalytics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAnalytics();
            }
        });

        toFinancialAssets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFinancialAssets();
            }
        });




    }
    public void openShoppingList() {
        Intent openShoppingListInt = new Intent(this, ShoppingListActivity.class);
        startActivity(openShoppingListInt);
    }

    public void openAddTransaction() {
        Intent openAddTransactionInt = new Intent(this, TransactionConstructorActivity.class);
        startActivity(openAddTransactionInt);
    }
    public void openShowTransactions() {
        Intent openShowTransactionInt = new Intent(this, QueryTransactionsActivity.class);
        startActivity(openShowTransactionInt);
    }

    public void openAnalytics() {
        Intent openAnalytics = new Intent(this, AnalyticsMainActivity.class);
        startActivity(openAnalytics);
    }

    public void openFinancialAssets() {
        Intent openFinancialAssets = new Intent(this, FinancialAssetsActivity.class);
        startActivity(openFinancialAssets);
    }

    public void bindViews() {
        toShoppingList = (Button) findViewById(R.id.MM_toShoppingListBtn);
        toAddTransaction = (Button) findViewById(R.id.MM_toAddTransactionBtn);
        toShowTransactions = (Button) findViewById(R.id.MM_toShowTransactionsBtn);
        toAnalytics = (Button) findViewById(R.id.MM_toAnalyticsBtn);
        toFinancialAssets = (Button) findViewById(R.id.MM_toFinancialAssets);
    }
}
