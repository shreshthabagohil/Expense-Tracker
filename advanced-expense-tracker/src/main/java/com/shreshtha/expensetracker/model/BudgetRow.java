package com.shreshtha.expensetracker.model;

public class BudgetRow {

    private final String category;
    private final double budget;
    private final double spent;

    public BudgetRow(String category, double budget, double spent) {
        this.category = category;
        this.budget = budget;
        this.spent = spent;
    }

    public String getCategory() {
        return category;
    }

    public double getBudget() {
        return budget;
    }

    public double getSpent() {
        return spent;
    }

    public double getRemaining() {
        return budget - spent;
    }
}
