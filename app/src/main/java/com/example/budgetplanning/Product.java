package com.example.budgetplanning;

import java.util.ArrayList;

public class Product {
    private String name;
    private String defaultCategory;
    private String manufacturer;
    private ArrayList<Integer> associatedBarCodes;
    private ArrayList<String> associatedTransactionsKeys;
    private String parentSubcategoryKey;



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

