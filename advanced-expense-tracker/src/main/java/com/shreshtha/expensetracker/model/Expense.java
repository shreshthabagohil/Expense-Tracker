package com.shreshtha.expensetracker.model;

public class Expense {

    private int id;
    private double amount;
    private String category;
    private String date;
    private String mood;
    private String description;

    // used when reading from database
    public Expense(int id,double amount,String category,String date,String mood,String description) {
        this.id=id;
        this.amount=amount;
        this.category=category;
        this.date=date;
        this.mood=mood;
        this.description=description;
    }

    // used when creating new expense
    public Expense(double amount,String category,String date,String mood,String description) {
        this.amount=amount;
        this.category=category;
        this.date=date;
        this.mood=mood;
        this.description=description;
    }

    public int getId() { return id; }

    public double getAmount() { return amount; }

    public String getCategory() { return category; }

    public String getDate() { return date; }

    public String getMood() { return mood; }

    public String getDescription() { return description; }
}
