package com.example.budgetplanning;

import android.widget.Toast;

public class Transaction {
    private String category;
    private String name;
    private String typeOfQuantity;
    private double quantity;
    private String typeOfCurrency;
    private double cost;
    private boolean service=false;

    int date;

    Transaction() { }

    Transaction(String setCategory, String setName, String setTypeOfQuantity, double setQuantity,
                String setTypeOfCurrency, double setCost, int setDate) {
        setCategory(setCategory);
        setName(setName);
        setTypeOfQuantity(setTypeOfQuantity);
        setQuantity(setQuantity);
        setTypeOfCurrency(setTypeOfCurrency);
        setCost(setCost);
        setDate(setDate);
        setIsService(false);
    }

    Transaction(String setCategory, String setName, String setTypeOfQuantity, double setQuantity,
                String setTypeOfCurrency, double setCost, int setDate, boolean service) {
        setCategory(setCategory);
        setName(setName);
        setTypeOfQuantity(setTypeOfQuantity);
        setQuantity(setQuantity);
        setTypeOfCurrency(setTypeOfCurrency);
        setCost(setCost);
        setDate(setDate);
        setIsService(service);
    }

    Transaction(String setCategory, String setName, String setTypeOfCurrency,
                double setCost, int setDate, boolean service) {
        setCategory(setCategory);
        setName(setName);
        setTypeOfCurrency(setTypeOfCurrency);
        setQuantity(0);
        setTypeOfQuantity("");
        setCost(setCost);
        setDate(setDate);
        setIsService(service);
    }



    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTypeOfQuantity() {
        return typeOfQuantity;
    }

    public void setTypeOfQuantity(String typeOfQuantity) {
        this.typeOfQuantity = typeOfQuantity;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public String getTypeOfCurrency() {
        return typeOfCurrency;
    }

    public void setTypeOfCurrency(String typeOfCurrency) {
        this.typeOfCurrency = typeOfCurrency;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }
    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        DateQueryHelper dateHelper = new DateQueryHelper();
        if (true) {this.date = date; }
    }

    public boolean isService() {
        return service;
    }

    public void setIsService(boolean service) {
        service = service;
    }
}
