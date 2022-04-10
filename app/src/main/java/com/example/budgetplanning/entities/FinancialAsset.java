package com.example.budgetplanning.entities;

public class FinancialAsset {
    protected String typeOfAsset;
    protected double remainingAmount;
    protected String typeOfCurrency;

    public FinancialAsset() {}

    public double getRemainingAmount() {return  this.remainingAmount;}
    public String getTypeOfCurrency() {return this.typeOfCurrency;}
    public String getTypeOfAsset() {return  this.typeOfAsset;}
}
