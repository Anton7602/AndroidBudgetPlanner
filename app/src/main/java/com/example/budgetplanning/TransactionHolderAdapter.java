package com.example.budgetplanning;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TransactionHolderAdapter extends RecyclerView.Adapter<TransactionHolderAdapter.TransactionHolderViewHolder> {
    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
    private ArrayList<TransactionHolder> adapterTransactionList;

    public static class TransactionHolderViewHolder extends RecyclerView.ViewHolder {
        public TextView transactionNumber;
        public TextView transactionDate;
        public TextView transactionSum;
        RecyclerView transactionsToShow;

        public TransactionHolderViewHolder(@NonNull View itemView) {
            super(itemView);
            transactionNumber = itemView.findViewById(R.id.weekNumberTextView);
            transactionDate=itemView.findViewById(R.id.datesTextView);
            transactionSum=itemView.findViewById(R.id.sumView11);
            transactionsToShow = itemView.findViewById(R.id.transactionsHeld);
        }
    }

    public  TransactionHolderAdapter(ArrayList<TransactionHolder> transactionList) {
        adapterTransactionList=transactionList;
    }

    @NonNull
    @Override
    public TransactionHolderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction_holder, parent, false);
        TransactionHolderViewHolder tvh = new TransactionHolderViewHolder(v);
        return tvh;
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionHolderViewHolder holder, int position) {
        TransactionHolder transactionHolder = adapterTransactionList.get(position);
        holder.transactionNumber.setText("Неделя: " + String.valueOf(transactionHolder.getNumber()));
        holder.transactionSum.setText(String.format("%,.2f",transactionHolder.getSumOfTransactions())+" ₽");
        holder.transactionDate.setText(String.valueOf(transactionHolder.getDateFromTo()));
        LinearLayoutManager layoutManager = new LinearLayoutManager(holder.transactionsToShow.getContext(), LinearLayoutManager.VERTICAL, false);
        layoutManager.setInitialPrefetchItemCount(transactionHolder.getListOfTransactions().size());
        TransactionAdapter transactionAdapter = new TransactionAdapter(transactionHolder.getListOfTransactions());
        holder.transactionsToShow.setLayoutManager(layoutManager);
        holder.transactionsToShow.setAdapter(transactionAdapter);
        holder.transactionsToShow.setRecycledViewPool(viewPool);
    }

    @Override
    public int getItemCount() {
        return adapterTransactionList.size();
    }
}
