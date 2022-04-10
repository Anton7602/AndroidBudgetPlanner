package com.example.budgetplanning.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.budgetplanning.entities.FinancialAsset;
import com.example.budgetplanning.adapters.FinancialAssetAdapter;
import com.example.budgetplanning.entities.FinancialAssetCard;
import com.example.budgetplanning.entities.FinancialAssetCash;
import com.example.budgetplanning.R;
import com.example.budgetplanning.utility.getExchangeRate;
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
    private TextView sumOfAssetsTextView;
    private ProgressBar progressBar;
    private double sumOfAssets, usdRate=0, eurRate=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_financial_assets);
        bindViews();
        setUpRecyclerViews();
        sumOfAssets=0;
        getExchangeRate currentExchangeRate = new getExchangeRate();
        currentExchangeRate.execute();
        int exchangeRateReceivingRuntime =0;
        try {
            while (!currentExchangeRate.isExchangeRateReceived() && exchangeRateReceivingRuntime <1000) {
                exchangeRateReceivingRuntime++;
                Thread.sleep(2);
            }
            if (currentExchangeRate.isExchangeRateReceived()) {
                usdRate=currentExchangeRate.getUSD();
                eurRate=currentExchangeRate.getEUR();
            } else {
                Toast.makeText(getApplicationContext(), "Данные о курсах валют не получены за отведённое время", Toast.LENGTH_SHORT).show();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Активы");
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listOfAssets.clear();
                for (DataSnapshot currentSnapshot : snapshot.getChildren()) {
                    FinancialAsset currentAsset = currentSnapshot.getValue(FinancialAsset.class);
                    if (currentAsset.getTypeOfCurrency().equals("₽")) {
                        sumOfAssets += currentAsset.getRemainingAmount();
                    }
                    else if (currentAsset.getTypeOfCurrency().equals("$")) {
                        sumOfAssets += currentAsset.getRemainingAmount()*usdRate;
                    }
                    else if (currentAsset.getTypeOfCurrency().equals("€")) {
                        sumOfAssets += currentAsset.getRemainingAmount()*eurRate;
                    }
                    if (currentAsset.getTypeOfAsset().equals("card")) {
                        FinancialAssetCard currentCard = currentSnapshot.getValue(FinancialAssetCard.class);
                        listOfAssets.add(currentCard);
                    }
                    if (currentAsset.getTypeOfAsset().equals("cash")) {
                        FinancialAssetCash currentCash = currentSnapshot.getValue(FinancialAssetCash.class);
                        listOfAssets.add(currentCash);
                    }
                }
                mAdapter.notifyDataSetChanged();
                sumOfAssetsTextView.setText(String.format("%,.2f", sumOfAssets)+"₽");
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void bindViews() {
        mRecycleView = (RecyclerView) findViewById(R.id.FAa_FinancialAssetsRecyclerView);
        sumOfAssetsTextView = (TextView) findViewById(R.id.FAa_sumOfAssets);
        progressBar = (ProgressBar) findViewById(R.id.FAa_ProgressBar);
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