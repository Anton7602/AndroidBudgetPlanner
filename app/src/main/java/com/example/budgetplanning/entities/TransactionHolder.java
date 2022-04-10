package com.example.budgetplanning.entities;

import java.util.ArrayList;

public class TransactionHolder {
    private int number;
    private ArrayList<Transaction> listOfTransactions;
    private ArrayList<String> listOfKeys;
    private String dateFromTo;
    private double sumOfTransactions;
    private boolean isExpanded;

    public TransactionHolder(ArrayList<Transaction> listOfTransactions, ArrayList<String> listOfKeys,String dateFromTo, int number) {
        setDateFromTo(dateFromTo);
        setListOfTransactions(listOfTransactions);
        setListOfKeys(listOfKeys);
        setNumber(number);
        isExpanded=false;
        sumOfTransactions = 0;
        for (Transaction transaction : listOfTransactions) {
            sumOfTransactions += transaction.getCost();
        }
    }

    private void setListOfKeys(ArrayList<String> listOfKeys) {
        this.listOfKeys = listOfKeys;
    }

    public ArrayList<String> getListOfKeys() {
        return listOfKeys;
    }

    public double getSumOfTransactions() {
        return sumOfTransactions;
    }

    public void setSymOfTransactions(double symOfTransactions) {
        this.sumOfTransactions = symOfTransactions;
    }

    public String getDateFromTo() {
        return dateFromTo;
    }

    public void setDateFromTo(String dateFromTo) {
        this.dateFromTo = dateFromTo;
    }

    public ArrayList<Transaction> getListOfTransactions() {
        return listOfTransactions;
    }

    public void setListOfTransactions(ArrayList<Transaction> listOfTransactions) {
        this.listOfTransactions = listOfTransactions;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
