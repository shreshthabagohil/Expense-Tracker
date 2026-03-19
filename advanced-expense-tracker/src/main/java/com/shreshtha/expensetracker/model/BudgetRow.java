package com.shreshtha.expensetracker.model;

public class BudgetRow {

    private final String category;
    private final double originalBudget;
    private final double currentBudget;
    private final boolean edited;
    private final double spent;
    private final String dateModified; // ✅ ADDED NEW FIELD

    public BudgetRow(String category,
                     double originalBudget,
                     double currentBudget,
                     boolean edited,
                     double spent,
                     String dateModified) { // ✅ ADDED TO CONSTRUCTOR

        this.category = category;
        this.originalBudget = originalBudget;
        this.currentBudget = currentBudget;
        this.edited = edited;
        this.spent = spent;
        this.dateModified = dateModified; // ✅ ASSIGNED
    }

    public String getCategory() { return category; }
    public double getOriginalBudget() { return originalBudget; }
    public double getCurrentBudget() { return currentBudget; }
    public boolean isEdited() { return edited; }
    public double getSpent() { return spent; }
    public double getRemaining() { return currentBudget - spent; }
    public String getStatus() { return edited ? "Edited" : "Set"; }
    
    // ✅ ADDED GETTER FOR NEW FIELD
    public String getDateModified() { return dateModified != null ? dateModified : "N/A"; }

    public double getProgress() {
        if (currentBudget == 0) return 0;
        return spent / currentBudget;
    }
}
