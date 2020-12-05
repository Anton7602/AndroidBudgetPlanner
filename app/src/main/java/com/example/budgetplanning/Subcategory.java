package com.example.budgetplanning;

public class Subcategory {
    private String name;
    private String parentKey;
    private String associatedCategory;

    public Subcategory(String name, String associatedCategory) {
        setName(name);
        setAssociatedCategory(associatedCategory);
    }

    public Subcategory(String name, String associatedCategory, String parentKey) {
        setName(name);
        setAssociatedCategory(associatedCategory);
        setParentKey(parentKey);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParentKey() {
        return parentKey;
    }

    public void setParentKey(String parentKey) {
        this.parentKey = parentKey;
    }

    public String getAssociatedCategory() {
        return associatedCategory;
    }

    public void setAssociatedCategory(String associatedCategory) {
        this.associatedCategory = associatedCategory;
    }
}
