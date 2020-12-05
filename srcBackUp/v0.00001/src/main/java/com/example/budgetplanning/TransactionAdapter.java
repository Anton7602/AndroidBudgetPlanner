package com.example.budgetplanning;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {
    private ArrayList<Transaction> adapterTransactionList;

    public static class TransactionViewHolder extends RecyclerView.ViewHolder {
        public TextView transactionName;
        public TextView transactionCategory;
        public TextView transactionDate;
        public TextView transactionQuantity;
        public TextView transactionCost;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            transactionName = itemView.findViewById(R.id.transactionNameTextView);
            transactionCategory=itemView.findViewById(R.id.transactionCategoryTextView);
            transactionDate=itemView.findViewById(R.id.transactionDateOfTransactionTextView);
            transactionQuantity=itemView.findViewById(R.id.transactionQuantityTextView);
            transactionCost=itemView.findViewById(R.id.transactionCostTextView);
        }
    }

    public  TransactionAdapter(ArrayList<Transaction> transactionList) {
        adapterTransactionList=transactionList;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction_list, parent, false);
        TransactionViewHolder tvh = new TransactionViewHolder(v);
        return tvh;
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction newTransaction = adapterTransactionList.get(position);
        holder.transactionName.setText(newTransaction.getName());
        holder.transactionCategory.setText(newTransaction.getCategory());
        holder.transactionDate.setText(String.valueOf(newTransaction.getDate()).substring(6)+"/"+
                String.valueOf(newTransaction.getDate()).substring(4,6)+"/"+
                String.valueOf(newTransaction.getDate()).substring(0,4));
        holder.transactionQuantity.setText(newTransaction.getQuantity()+" "+newTransaction.getTypeOfQuantity());
        holder.transactionCost.setText(newTransaction.getCost()+" "+ newTransaction.getTypeOfCurrency());
    }

    @Override
    public int getItemCount() {
        return adapterTransactionList.size();
    }
}
