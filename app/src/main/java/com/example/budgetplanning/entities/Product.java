package com.example.budgetplanning.entities;

import java.util.ArrayList;
import java.util.Objects;

public class Product {
    private String name;
    private String defaultCategory;
    private String manufacturer;
    private double defaultQuantity;
    private String defaultQuantityType;
    private ArrayList<String> associatedBarCodes;
    private ArrayList<String> associatedBarCodesStores;
    private ArrayList<String> associatedTransactionsKeys;
    private String parentSubcategoryKey;

    public Product() {

    }

    public Product(String name, String defaultCategory, String manufacturer, double defaultQuantity, String defaultQuantityType) {
        this.name = name.substring(0,1).toUpperCase()+name.substring(1).toLowerCase();
        this.defaultCategory = defaultCategory;
        this.manufacturer = manufacturer.substring(0,1).toUpperCase()+manufacturer.substring(1).toLowerCase();
        this.defaultQuantity = defaultQuantity;
        this.defaultQuantityType = defaultQuantityType;
        this.associatedBarCodes = new ArrayList<>();
        this.associatedBarCodesStores = new ArrayList<>();
        this.associatedTransactionsKeys = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDefaultCategory() {
        return defaultCategory;
    }

    public void setDefaultCategory(String defaultCategory) {
        this.defaultCategory = defaultCategory;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public double getDefaultQuantity() {
        return defaultQuantity;
    }

    public void setDefaultQuantity(double defaultQuantity) {
        this.defaultQuantity = defaultQuantity;
    }

    public String getDefaultQuantityType() {
        return defaultQuantityType;
    }

    public void setDefaultQuantityType(String defaultQuantityType) {
        this.defaultQuantityType = defaultQuantityType;
    }

    public ArrayList<String> getAssociatedBarCodes() {
        return associatedBarCodes;
    }

    public void setAssociatedBarCodes(ArrayList<String> associatedBarCodes) {
        this.associatedBarCodes = associatedBarCodes;
    }

    public ArrayList<String> getAssociatedBarCodesStores() {
        return associatedBarCodesStores;
    }

    public void setAssociatedBarCodesStores(ArrayList<String> associatedBarCodesStores) {
        this.associatedBarCodesStores = associatedBarCodesStores;
    }

    public ArrayList<String> getAssociatedTransactionsKeys() {
        return associatedTransactionsKeys;
    }

    public void setAssociatedTransactionsKeys(ArrayList<String> associatedTransactionsKeys) {
        this.associatedTransactionsKeys = associatedTransactionsKeys;
    }

    public String getParentSubcategoryKey() {
        return parentSubcategoryKey;
    }

    public void setParentSubcategoryKey(String parentSubcategoryKey) {
        this.parentSubcategoryKey = parentSubcategoryKey;
    }

    public void addAssociatedBarCode(String barCode) {
        if (associatedBarCodes==null) {
            associatedBarCodes = new ArrayList<String>();
        }
        this.associatedBarCodes.add(barCode);
    }

    public void addAssociatedTransactionKey(String key) {
        if (associatedTransactionsKeys==null) {
            associatedTransactionsKeys = new ArrayList<String>();
        }
        this.associatedTransactionsKeys.add(key);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(this.name, product.name) &&
                Objects.equals(this.defaultCategory, product.defaultCategory) &&
                Objects.equals(this.manufacturer, product.manufacturer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, defaultCategory, manufacturer);
    }
}

