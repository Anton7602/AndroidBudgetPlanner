package com.example.budgetplanning;

public class Transaction {
    private String category;
    private String name;
    private String typeOfQuantity;
    private double quantity;
    private String typeOfCurrency;
    private double cost;
    //private String day;
    //private String month;
    //private String year;
    int date;

    Transaction() {

    }

    Transaction(String setCategory, String setName, String setTypeOfQuantity, double setQuantity,
                String setTypeOfCurrency, double setCost, String setDay, String setMonth, String setYear) {
        category=setCategory;
        name=setName;
        typeOfQuantity=setTypeOfQuantity;
        quantity = setQuantity;
        typeOfCurrency=setTypeOfCurrency;
        cost=setCost;
        //day=setDay;
        //month=setMonth;
        //year=setYear;
    }
    Transaction(String setCategory, String setName, String setTypeOfQuantity, double setQuantity,
                String setTypeOfCurrency, double setCost, int setDate) {
        setCategory(setCategory);
        setName(setName);
        setTypeOfQuantity(setTypeOfQuantity);
        setQuantity(setQuantity);
        setTypeOfCurrency(setTypeOfCurrency);
        setCost(setCost);
        setDate(setDate);
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
        this.date = date;
    }

   // public String getDay() {
        //return day;
 //   }

  //  public String getMonth() {
   //     return month;
   // }

  //  public void setMonth(String month) {
   //     this.month = month;
   // }

   // public String getYear() {
    //    return year;
  //  }

   // public void setYear(String year) {
   //     this.year = year;
   // }
}
