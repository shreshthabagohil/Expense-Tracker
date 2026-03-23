package com.shreshtha.expensetracker.model;

public class SavingsTransaction {
    private int id;
    private String username;
    private String type; // "DEPOSIT" or "WITHDRAWAL"
    private double amount;
    private String date;
    private String description;

    public SavingsTransaction(int id, String username, String type, double amount, String date, String description) {
        this.id = id;
        this.username = username;
        this.type = type;
        this.amount = amount;
        this.date = date;
        this.description = description;
    }

    // Getters
    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getType() { return type; }
    public double getAmount() { return amount; }
    public String getDate() { return date; }
    public String getDescription() { return description; }
}
