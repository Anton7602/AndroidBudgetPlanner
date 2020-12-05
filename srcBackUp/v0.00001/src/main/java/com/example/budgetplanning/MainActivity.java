package com.example.budgetplanning;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException ignored){}
        setContentView(R.layout.activity_main);
        Button toShoppingList = (Button) findViewById(R.id.toShoppingListBtn);
        Button toAddTransaction = (Button) findViewById(R.id.toAddTransactionBtn);
        Button toShowTransactions = (Button) findViewById(R.id.toShowTransactions);

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
}
