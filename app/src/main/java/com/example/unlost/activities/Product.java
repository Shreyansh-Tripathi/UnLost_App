package com.example.unlost.activities;

public class Product {
    private String category,subcategory,url;

    public Product(String category, String subcategory, String url) {
        this.category = category;
        this.subcategory = subcategory;
        this.url = url;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSubcategory() {
        return subcategory;
    }

    public void setSubcategory(String subcategory) {
        this.subcategory = subcategory;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
