package com.example.budgetplanning;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.JsonReader;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.util.IOUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;


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
