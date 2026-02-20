package com.shreshtha.expensetracker.model;

public class Budget {

    private final String category;

    // Original value when first set
    private final double originalLimit;

    // Current usable value (may change once)
    private double currentLimit;

    // Allow only one edit
    private boolean edited;

    public Budget(String category, double limit) {
        this.category = category;
        this.originalLimit = limit;
        this.currentLimit = limit;
        this.edited = false;
    }

    public String getCategory() {
        return category;
    }

    public double getOriginalLimit() {
        return originalLimit;
    }

    public double getCurrentLimit() {
        return currentLimit;
    }

    public boolean isEdited() {
        return edited;
    }

    // Business rule: can edit only once
    public void edit(double newLimit) {

        if (edited) {
            throw new IllegalStateException("Budget already edited once");
        }

        this.currentLimit = newLimit;
        this.edited = true;
    }

    // Used by UI
    public String getStatus() {
        return edited? "EDITED": "SET";
    }
}
