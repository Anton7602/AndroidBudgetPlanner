package com.example.budgetplanning;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import static androidx.core.content.ContextCompat.startActivity;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {
    private ArrayList<Transaction> adapterTransactionList;
    private ArrayList<String> adapterKeysList;

    public static class TransactionViewHolder extends RecyclerView.ViewHolder {
        public TextView transactionName;
        public TextView transactionCategory;
        public TextView transactionDate;
        public TextView transactionQuantity;
        public TextView transactionCost;
        public String transactionKey;

        final static public String EXTRA_PRODUCT_NAME = "TransactionName";
        final static public String EXTRA_PRODUCT_CATEGORY = "TransactionCategory";
        final static public String EXTRA_PRODUCT_DATE = "TransactionDate";
        final static public String EXTRA_PRODUCT_QUANTITY = "TransactionQuantity";
        final static public String EXTRA_PRODUCT_COST = "TransactionCost";
        final static public String EXTRA_PRODUCT_KEY = "TransactionKey";

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            transactionName = itemView.findViewById(R.id.transactionNameTextView);
            transactionCategory=itemView.findViewById(R.id.transactionCategoryTextView);
            transactionDate=itemView.findViewById(R.id.transactionDateOfTransactionTextView);
            transactionQuantity=itemView.findViewById(R.id.transactionQuantityTextView);
            transactionCost=itemView.findViewById(R.id.transactionCostTextView);
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (transactionKey.length()>1) {
                    //Toast.makeText(view.getContext(), transactionKey.toString(), Toast.LENGTH_LONG).show();
                    Intent openTransactionEdit = new Intent(view.getContext(), TransactionEditActivity.class);
                    openTransactionEdit.putExtra(EXTRA_PRODUCT_NAME, transactionName.getText().toString());
                    openTransactionEdit.putExtra(EXTRA_PRODUCT_CATEGORY, transactionCategory.getText().toString());
                    openTransactionEdit.putExtra(EXTRA_PRODUCT_DATE, transactionDate.getText().toString());
                    openTransactionEdit.putExtra(EXTRA_PRODUCT_QUANTITY, transactionQuantity.getText().toString());
                    openTransactionEdit.putExtra(EXTRA_PRODUCT_COST, transactionCost.getText().toString());
                    openTransactionEdit.putExtra(EXTRA_PRODUCT_KEY, transactionKey);
                    startActivity(view.getContext(), openTransactionEdit, null);
                    }
                    return false;
                }
            });
        }
    }


    public  TransactionAdapter(ArrayList<Transaction> transactionList, ArrayList<String> keysList) {
        adapterTransactionList=transactionList;
        adapterKeysList=keysList;
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
        DateQueryHelper dateHelper = new DateQueryHelper();
        Transaction newTransaction = adapterTransactionList.get(position);
        holder.transactionName.setText(newTransaction.getName());
        holder.transactionCategory.setText(newTransaction.getCategory());
        holder.transactionDate.setText(dateHelper.dateParseToString(newTransaction.getDate()));
        if (!newTransaction.isService()) {
            holder.transactionQuantity.setText(newTransaction.getQuantity() + " " + newTransaction.getTypeOfQuantity());
            holder.transactionQuantity.setVisibility(View.VISIBLE);
        }
        if (newTransaction.isService()) {
            holder.transactionQuantity.setVisibility(View.GONE);
        }
        if (adapterKeysList.size()>0) {
            holder.transactionKey = adapterKeysList.get(position);
        }
        holder.transactionCost.setText(String.format("%,.2f",newTransaction.getCost())+" "+ newTransaction.getTypeOfCurrency());
    }


    @Override
    public int getItemCount() {
        return adapterTransactionList.size();
    }
}
