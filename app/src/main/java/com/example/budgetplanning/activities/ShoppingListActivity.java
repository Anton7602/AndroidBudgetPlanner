package com.example.budgetplanning.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.budgetplanning.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

//Activity to add records and view Shopping List
public class ShoppingListActivity extends AppCompatActivity {
    final static public String EXTRA_PRODUCT_NAME = "ProductName";

    private Button addDataButton;
    private EditText nameField;
    private ListView shoppingData;
    private DatabaseReference mDatabase;
    private ArrayList<String> mShoppingList = new ArrayList<>();
    private ArrayList<String> mShoppingKey = new ArrayList<>();
    private ArrayAdapter<String> arrayAdapter;
    private ProgressBar loadingProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
	    //Initializing Activity, setting up views, views parameters and database reference
        setContentView(R.layout.activity_shopping_list);
	    bindViews();
	    setUpListViews();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Список покупок");

	    //Defining database methods to retrive and manage data from database
        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String value = snapshot.getValue(String.class);
                mShoppingList.add(value);
                arrayAdapter.notifyDataSetChanged();
                String key = snapshot.getKey();
                mShoppingKey.add(key);
                loadingProgressBar.setVisibility(View.GONE);
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

	//Defining on click listener on Add Data Button to push content of
	//editText to shopping list database
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

	//Defining onItemClickListener fo shopping List to call transaction constructor
	//and retrieve data from database (and ListView)
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

        shoppingData.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder editShoppingListDialog = new AlertDialog.Builder(ShoppingListActivity.this);
                editShoppingListDialog.setTitle("Редактировать элемент списка");
                final int currentPos = position;
                final EditText shoppingListItem = new EditText(ShoppingListActivity.this);
                editShoppingListDialog.setView(shoppingListItem);
                shoppingListItem.setText(mShoppingList.get(position));
                editShoppingListDialog.setPositiveButton("Подтвердить значение", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mDatabase.child(mShoppingKey.get(currentPos)).setValue(shoppingListItem.getText().toString());
                    }
                });
                editShoppingListDialog.setNegativeButton("Удалить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mDatabase.child(mShoppingKey.get(currentPos)).removeValue();
                        dialog.cancel();
                    }
                });
                AlertDialog dialog = editShoppingListDialog.create();
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.applicationBackground)));
                dialog.show();
                return true;
            }
        });
    }

    private void bindViews() {
    addDataButton = (Button) findViewById(R.id.ShL_addToListButton);
	nameField = (EditText) findViewById(R.id.ShL_nameField);
	shoppingData = (ListView) findViewById(R.id.ShL_mShoppingData);
	loadingProgressBar = (ProgressBar) findViewById(R.id.ShL_progressBar);
    }

    private void setUpListViews() {
	    arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mShoppingList);
        shoppingData.setAdapter(arrayAdapter);
    }
}