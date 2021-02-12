package com.example.budgetplanning;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FinancialAssetsActivity extends AppCompatActivity {
    private ArrayList<FinancialAsset> listOfAssets;
    private RecyclerView mRecycleView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_financial_assets);
        bindViews();
        setUpRecyclerViews();
        //FinancialAssetCard testCard = new FinancialAssetCard(1234, 85425.54, "₽", "Сбербанк", 05, 2022, "Visa");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Активы");
        //mDatabase.push().setValue(testCard);
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listOfAssets.clear();
                for (DataSnapshot currentSnapshot : snapshot.getChildren()) {
                    FinancialAssetCard currentCard = currentSnapshot.getValue(FinancialAssetCard.class);
                    listOfAssets.add(currentCard);
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void bindViews() {
        mRecycleView = (RecyclerView) findViewById(R.id.FAa_FinancialAssetsRecyclerView);
    }

    private void setUpRecyclerViews() {
        listOfAssets = new ArrayList<FinancialAsset>();
        mRecycleView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new FinancialAssetAdapter(listOfAssets);
        mRecycleView.setLayoutManager(mLayoutManager);
        mRecycleView.setAdapter(mAdapter);
    }
}