package com.example.budgetplanning;

public class FinancialAssetCard extends FinancialAsset {
    private int lastDigitsOfNumber;
    private String bankName;
    private int expMonth;
    private int expYear;
    private String paymentMethod;

    public FinancialAssetCard() {

    }

    public FinancialAssetCard(int setLastDigitsOfNumber, double setRemainingAmount, String setTypeOfCurrency, String setBankName, int setExpMonth, int setExpYear, String setPaymentMethod) {
        this.lastDigitsOfNumber=setLastDigitsOfNumber;
        this.remainingAmount=setRemainingAmount;
        this.typeOfCurrency=setTypeOfCurrency;
        this.bankName=setBankName;
        this.expMonth=setExpMonth;
        this.expYear=setExpYear;
        this.paymentMethod=setPaymentMethod;
    }

    public int getLastDigitsOfNumber() {return this.lastDigitsOfNumber;}
    public String getBankName() {return this.bankName;}
    public int getExpMonth() {return  this.expMonth;}
    public int getExpYear() {return  this.expYear;}
    public String getPaymentMethod() {return  this.paymentMethod;}
}
