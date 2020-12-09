package com.example.budgetplanning;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


//Main activity is a navigation main menu to the rest of th application


public class MainActivity extends AppCompatActivity {

    Button toShoppingList, toAddTransaction, toShowTransactions, toAnalytics, toProductConstructor;

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

        toProductConstructor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openProductConstructor();
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

    public void openProductConstructor() {
        Intent openProductConstructor = new Intent(this, ProductConstructorActivity.class);
        startActivity(openProductConstructor);
    }

    public void bindViews() {
        toShoppingList = (Button) findViewById(R.id.MM_toShoppingListBtn);
        toAddTransaction = (Button) findViewById(R.id.MM_toAddTransactionBtn);
        toShowTransactions = (Button) findViewById(R.id.MM_toShowTransactionsBtn);
        toAnalytics = (Button) findViewById(R.id.MM_toAnalyticsBtn);
        toProductConstructor = (Button) findViewById(R.id.MM_toProductConstructorBtn);
    }
}
