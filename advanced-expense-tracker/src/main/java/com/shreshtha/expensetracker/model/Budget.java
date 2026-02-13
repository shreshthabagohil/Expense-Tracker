package com.shreshtha.expensetracker.model;

public class Budget 
{
    
    private String category;
    private double limit;

    public Budget (String category,double limit)
    {
        this.category=category;
        this.limit=limit;
    }

    public String getCategory()
    {
        return category;
    }

    public double getLimit() 
    {
        return limit;
    }
}
