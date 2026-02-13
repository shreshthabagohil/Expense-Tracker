package com.shreshtha.expensetracker.model;

public class Expense 
{
    private double amount;
    private String category;
    private String date;
    private String mood;
    private String descripton;

    public Expense(double amount, String category, String date,String mood,String description) {
        this.amount = amount;
        this.category = category;
        this.date = date;
        this.mood=mood;
        this.descripton=description;
    }

    public double getAmount() {
        return amount;
    }

    public String getCategory() {
        return category;
    }

    public String getDate() {
        return date;
    }

    public String getDescription()
    {
        return descripton;
    }

   @Override
    public String toString() {
    return amount + " | " + category + " | " + date + " | " + mood+" | " + descripton;
     }


    public String getMood() {
        return mood;
    }
    
}
