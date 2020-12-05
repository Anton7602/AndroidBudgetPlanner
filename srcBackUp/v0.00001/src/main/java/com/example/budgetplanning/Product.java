package com.example.budgetplanning;

public class Product {
    private String name;

    Product () {
        name = "Я тестовый объект";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
