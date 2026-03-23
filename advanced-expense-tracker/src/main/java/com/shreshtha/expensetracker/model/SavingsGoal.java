package com.shreshtha.expensetracker.model;

public class SavingsGoal {
    private String username;
    private String month;
    private double targetAmount;
    private String frequency; // "Daily", "Weekly", "Monthly"

    public SavingsGoal(String username, String month, double targetAmount, String frequency) {
        this.username = username;
        this.month = month;
        this.targetAmount = targetAmount;
        this.frequency = frequency;
    }

    public String getUsername() { return username; }
    public String getMonth() { return month; }
    public double getTargetAmount() { return targetAmount; }
    public String getFrequency() { return frequency; }
}
