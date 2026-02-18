package com.shreshtha.expensetracker.model;

public class BudgetRow {

    private final String category;
    private final double originalBudget;  // first value set
    private final double currentBudget;   // latest value (edited or not)
    private final double spent;
    private final boolean edited;         // whether user edited once

    public BudgetRow(
            String category,
            double originalBudget,
            double currentBudget,
            double spent,
            boolean edited
    ) {
        this.category = category;
        this.originalBudget = originalBudget;
        this.currentBudget = currentBudget;
        this.spent = spent;
        this.edited = edited;
    }

    public String getCategory() {
        return category;
    }

    public double getOriginalBudget() {
        return originalBudget;
    }

    public double getCurrentBudget() {
        return currentBudget;
    }

    public double getSpent() {
        return spent;
    }

    public double getRemaining() {
        return currentBudget - spent;
    }

    public boolean isEdited() {
        return edited;
    }

    public String getStatus() {
        return edited ? "Edited" : "Set";
    }
}
