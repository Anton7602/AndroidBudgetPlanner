package com.example.budgetplanning;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FinancialAssetAdapter extends RecyclerView.Adapter<FinancialAssetAdapter.FinancialAssetCardViewHolder> {
    private ArrayList<FinancialAsset> FinancialAssetList;

    public FinancialAssetAdapter(ArrayList<FinancialAsset> inputFinancialAssetsList) {
        FinancialAssetList=inputFinancialAssetsList;
    }

    @NonNull
    @Override
    public FinancialAssetCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_financial_asset_card, parent, false);
        FinancialAssetCardViewHolder facvh = new FinancialAssetCardViewHolder(v);
        return facvh;
    }

    @Override
    public void onBindViewHolder(@NonNull FinancialAssetCardViewHolder holder, int position) {
        FinancialAssetCard currentFinancialAsset = (FinancialAssetCard) FinancialAssetList.get(position);
        if (currentFinancialAsset.getExpMonth()<10)
        {
            holder.expDate.setText("0"+currentFinancialAsset.getExpMonth()+"/"+String.valueOf(currentFinancialAsset.getExpYear()).substring(2));
        }
        holder.bankName.setText(currentFinancialAsset.getBankName());
        holder.remainingAmount.setText(String.valueOf(currentFinancialAsset.getRemainingAmount())+" "+currentFinancialAsset.getTypeOfCurrency());
        holder.lastDigitsOfNumber.setText("****  ****  ****  "+currentFinancialAsset.getLastDigitsOfNumber());
    }

    @Override
    public int getItemCount() {
        return FinancialAssetList.size();
    }

    public static class FinancialAssetCardViewHolder extends RecyclerView.ViewHolder {
        public TextView bankName, remainingAmount, lastDigitsOfNumber, expDate;
        public ImageView paymentMethod;

        public FinancialAssetCardViewHolder(@NonNull View itemView) {
            super(itemView);
            bankName = itemView.findViewById(R.id.FAc_cardBankName);
            remainingAmount = itemView.findViewById(R.id.FAc_cardRemainingAmount);
            lastDigitsOfNumber = itemView.findViewById(R.id.FAc_cardNumberTextView);
            expDate = itemView.findViewById(R.id.FAc_cardExpireDateTextView);
            paymentMethod=itemView.findViewById(R.id.FAc_paymentSystemLogo);

        }
    }
}
