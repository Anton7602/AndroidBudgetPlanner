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
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ShoppingListActivity extends AppCompatActivity {
    final static public String EXTRA_PRODUCT_NAME = "ProductName";

    private DatabaseReference mDatabase;
    private ArrayList<String> mShoppingList = new ArrayList<>();
    private ArrayList<String> mShoppingKey = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException ignored){}
        setContentView(R.layout.activity_shopping_list);

        Button addDataButton = (Button) findViewById(R.id.addToListButton);
        final EditText nameField = (EditText) findViewById(R.id.nameField);
        ListView shoppingData = (ListView) findViewById(R.id.mShoppingData);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Список покупок");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mShoppingList);
        shoppingData.setAdapter(arrayAdapter);

        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String value = snapshot.getValue(String.class);
                mShoppingList.add(value);
                arrayAdapter.notifyDataSetChanged();
                String key = snapshot.getKey();
                mShoppingKey.add(key);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String value = snapshot.getValue(String.class);
                String key = snapshot.getKey();
                int index = mShoppingKey.indexOf(key);
                mShoppingList.set(index, value);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                String key = snapshot.getKey();
                int index = mShoppingKey.indexOf(key);
                mShoppingList.remove(index);
                mShoppingKey.remove(key);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        addDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameField.getText().toString().trim();
                if (name.length()>2) {
                    name = name.substring(0, 1).toUpperCase() + name.substring(1);
                    nameField.setText("");
                    mDatabase.push().setValue(name);
                }
                else {
                    Toast.makeText(getApplicationContext(), "Некорректное имя элемента", Toast.LENGTH_LONG).show();
                }
            }
        });

        shoppingData.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id) {
                String deletingItem = ((TextView) itemClicked).getText().toString();
                mDatabase.child(mShoppingKey.get(position)).removeValue();
                Intent startTransactionConstructorInt = new Intent(getApplicationContext(), TransactionConstructorActivity.class);
                startTransactionConstructorInt.putExtra(EXTRA_PRODUCT_NAME, deletingItem);
                startActivity(startTransactionConstructorInt);
            }
        });
    }
}