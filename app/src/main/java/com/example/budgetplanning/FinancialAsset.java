package com.example.budgetplanning;

public class FinancialAsset {
    protected double remainingAmount;
    protected String typeOfCurrency;

    public FinancialAsset() {}

    public double getRemainingAmount() {return  this.remainingAmount;}
    public String getTypeOfCurrency() {return this.typeOfCurrency;}
}
