package com.example.budgetplanning;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FinancialAssetAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<FinancialAsset> FinancialAssetList;

    public FinancialAssetAdapter(ArrayList<FinancialAsset> inputFinancialAssetsList) {
        FinancialAssetList=inputFinancialAssetsList;
    }

    @Override
    public int getItemViewType(int position) {
        if (FinancialAssetList.get(position) instanceof FinancialAssetCard) {
            return 0;
        }
        else if (FinancialAssetList.get(position) instanceof FinancialAssetCash)
        {
            return 2;
        }
        return -1;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case 0: return new FinancialAssetCardViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_financial_asset_card, parent, false));
            case 2: return new FinancialAssetCashViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_financial_asset_cash, parent, false));
        }
        return new FinancialAssetCardViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_financial_asset_card, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        switch (viewHolder.getItemViewType()) {
            case 0:
                FinancialAssetCardViewHolder cardHolder = (FinancialAssetCardViewHolder) viewHolder;
                FinancialAssetCard currentFinancialAssetCard = (FinancialAssetCard) FinancialAssetList.get(position);
                if (currentFinancialAssetCard.getExpMonth()<10)
                {
                    cardHolder.expDate.setText("0"+currentFinancialAssetCard.getExpMonth()+"/"+String.valueOf(currentFinancialAssetCard.getExpYear()).substring(2));
                }
                cardHolder.bankName.setText(currentFinancialAssetCard.getBankName());
                cardHolder.remainingAmount.setText(String.format("%,.2f", currentFinancialAssetCard.getRemainingAmount())+" "+currentFinancialAssetCard.getTypeOfCurrency());
                cardHolder.lastDigitsOfNumber.setText("****  ****  ****  "+currentFinancialAssetCard.getLastDigitsOfNumber());
                break;
            case 2:
                FinancialAssetCashViewHolder cashHolder = (FinancialAssetCashViewHolder) viewHolder;
                FinancialAssetCash currentFinancialAssetCash = (FinancialAssetCash) FinancialAssetList.get(position);
                cashHolder.remainingAmount.setText(String.format("%,.2f", currentFinancialAssetCash.getRemainingAmount())+" "+currentFinancialAssetCash.getTypeOfCurrency());
                if (currentFinancialAssetCash.typeOfCurrency.equals("$")) {
                    cashHolder.backgroundImage.setImageResource(R.drawable.it_usd_background);

                }
                else if (currentFinancialAssetCash.typeOfCurrency.equals("€")) {
                    cashHolder.backgroundImage.setImageResource(R.drawable.it_eur_background);
                }
                break;
        }
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

    public static class FinancialAssetCashViewHolder extends RecyclerView.ViewHolder {
        public TextView remainingAmount;
        public ImageView backgroundImage;

        public FinancialAssetCashViewHolder(@NonNull View itemView) {
            super(itemView);
            remainingAmount = itemView.findViewById(R.id.FAcash_remainingAmount);
            backgroundImage = itemView.findViewById(R.id.FAcash_currencyBackground);
        }
    }
}
