package com.shreshtha.expensetracker.model;

import java.util.ArrayList;
import java.util.Map;

public class MonthlySummary {
    private ArrayList<Expense> monthlyExpenses;
    private double totalAmount;
    private Map<String, Double> categoryTotals;
    private String highestCategory;

    public MonthlySummary(ArrayList<Expense> monthlyExpenses,
                          double totalAmount,
                          Map<String, Double> categoryTotals,
                          String highestCategory) {
        this.monthlyExpenses = monthlyExpenses;
        this.totalAmount = totalAmount;
        this.categoryTotals = categoryTotals;
        this.highestCategory = highestCategory;
    }

    public ArrayList<Expense> getMonthlyExpenses() { return monthlyExpenses; }
    public double getTotalAmount() { return totalAmount; }
    public Map<String, Double> getCategoryTotals() { return categoryTotals; }
    public String getHighestCategory() { return highestCategory; }
}
