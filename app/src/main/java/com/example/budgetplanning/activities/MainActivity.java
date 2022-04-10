package com.example.budgetplanning.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.budgetplanning.R;
import com.example.budgetplanning.roomdb.App;
import com.example.budgetplanning.roomdb.LocalDatabase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


//Main activity is a navigation main menu to the rest of th application


public class MainActivity extends AppCompatActivity {

    private Button toShoppingList, toAddTransaction, toShowTransactions, toAnalytics ,toFinancialAssets, toCatalog;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindViews();
        LocalDatabase localDatabase = App.getInstance().getLocalDatabase();
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            mAuth.signInWithEmailAndPassword("Strateg7602@gmail.com", "strat7602").addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                    } else {
                    }
                }
            });
        }

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

        toCatalog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCatalog();
            }
        });
    }

    private void openShoppingList() {
        Intent openShoppingListInt = new Intent(this, ShoppingListActivity.class);
        startActivity(openShoppingListInt);
    }

    private void openAddTransaction() {
        Intent openAddTransactionInt = new Intent(this, TransactionConstructorActivity.class);
        startActivity(openAddTransactionInt);
    }
    private void openShowTransactions() {
        Intent openShowTransactionInt = new Intent(this, QueryTransactionsActivity.class);
        startActivity(openShowTransactionInt);
    }

    private void openAnalytics() {
        Intent openAnalytics = new Intent(this, AnalyticsMainActivity.class);
        startActivity(openAnalytics);
    }

    private void openFinancialAssets() {
        Intent openFinancialAssets = new Intent(this, FinancialAssetsActivity.class);
        startActivity(openFinancialAssets);
    }

    private void openCatalog() {
        Intent openCatalog = new Intent(this, TransactionConstructorActivityTest.class);
        startActivity(openCatalog);
    }

    private void bindViews() {
        toShoppingList = (Button) findViewById(R.id.MM_toShoppingListBtn);
        toAddTransaction = (Button) findViewById(R.id.MM_toAddTransactionBtn);
        toShowTransactions = (Button) findViewById(R.id.MM_toShowTransactionsBtn);
        toAnalytics = (Button) findViewById(R.id.MM_toAnalyticsBtn);
        toFinancialAssets = (Button) findViewById(R.id.MM_toFinancialAssets);
        toCatalog = (Button) findViewById(R.id.MM_toProductCatalog);
    }
}
