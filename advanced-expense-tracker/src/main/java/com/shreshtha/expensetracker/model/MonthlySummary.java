package com.shreshtha.expensetracker.model;

import java.util.List;
import java.util.Map;

public class MonthlySummary {

    private final List<Expense> expenses;
    private final double totalAmount;
    private final Map<String, Double> categoryTotals;
    private final String highestCategory;

    public MonthlySummary(
            List<Expense> expenses,
            double totalAmount,
            Map<String, Double> categoryTotals,
            String highestCategory
    ) {
        this.expenses = expenses;
        this.totalAmount = totalAmount;
        this.categoryTotals = categoryTotals;
        this.highestCategory = highestCategory;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public Map<String, Double> getCategoryTotals() {
        return categoryTotals;
    }

    public String getHighestCategory() {
        return highestCategory;
    }
}
