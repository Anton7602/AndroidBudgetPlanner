package com.example.budgetplanning.entities;

public class FinancialAssetCash extends  FinancialAsset {

    public FinancialAssetCash() {

    }

    public FinancialAssetCash(double setRemainingAmount, String setCurrencyType) {
        this.remainingAmount=setRemainingAmount;
        this.typeOfCurrency=setCurrencyType;
        this.typeOfAsset = "cash";
    }



}
