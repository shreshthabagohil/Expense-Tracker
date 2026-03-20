package com.shreshtha.expensetracker.controller;

import com.shreshtha.expensetracker.database.BudgetRepository;
import com.shreshtha.expensetracker.database.DatabaseManager;
import com.shreshtha.expensetracker.model.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public class BudgetService {

    private String username;
    private BudgetRepository budgetRepo;

    public BudgetService() {
        budgetRepo = new BudgetRepository();
    }

    public void setUser(String username) {
        this.username = username;
        // automatically create budgets for new month
        budgetRepo.rolloverBudget(username);
    }

    // ===============================
    // SET BUDGET (ONLY ONCE)
    // ===============================
    public void setBudgetOnce(String category, double limit) {
        String month = MonthKey.current().toString();

        try (Connection conn = DatabaseManager.connect()) {
            // Check if already exists
            String checkSql = "SELECT amount FROM budgets WHERE username = ? AND month = ? AND category = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setString(1, username);
                checkStmt.setString(2, month);
                checkStmt.setString(3, category);
                
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        throw new IllegalStateException("Budget already set for this category.");
                    }
                }
            }

            // ✅ Added date_modified to SQL
            String insertSql = "INSERT INTO budgets(username, month, category, amount, edited, date_modified) VALUES (?, ?, ?, ?, 0, ?)";
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setString(1, username);
                insertStmt.setString(2, month);
                insertStmt.setString(3, category);
                insertStmt.setDouble(4, limit);
                insertStmt.setString(5, LocalDate.now().toString()); // ✅ Save current date
                insertStmt.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ===============================
    // EDIT BUDGET (ONLY ONCE)
    // ===============================
    public void updateBudget(String category, double newLimit) {
        String month = MonthKey.current().toString();

        try (Connection conn = DatabaseManager.connect()) {
            String checkSql = "SELECT edited FROM budgets WHERE username = ? AND month = ? AND category = ?";
            
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setString(1, username);
                checkStmt.setString(2, month);
                checkStmt.setString(3, category);

                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (!rs.next()) {
                        throw new IllegalStateException("No budget for category.");
                    }

                    boolean alreadyEdited = rs.getInt("edited") == 1;

                    if (alreadyEdited) {
                        throw new IllegalStateException("Budget can only be edited once per month.");
                    }
                }
            }

            // ✅ Added date_modified to SQL
            String updateSql = "UPDATE budgets SET amount = ?, edited = 1, date_modified = ? WHERE username = ? AND month = ? AND category = ?";
            try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                updateStmt.setDouble(1, newLimit);
                updateStmt.setString(2, LocalDate.now().toString()); // ✅ Save current date
                updateStmt.setString(3, username);
                updateStmt.setString(4, month);
                updateStmt.setString(5, category);
                updateStmt.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ===============================
    // GET BUDGET ROWS FOR UI
    // ===============================
    public List<BudgetRow> getCurrentMonthBudgetRows(List<Expense> expenses) {
        List<BudgetRow> rows = new ArrayList<>();
        
        String dbMonthKey = MonthKey.current().toString(); // Used to find the budget in the DB
        String currentYearMonth = java.time.YearMonth.now().toString(); // Guarantees "2026-03" format for date matching!

        try (Connection conn = DatabaseManager.connect()) {
            String sql = "SELECT category, amount, edited, date_modified FROM budgets WHERE username = ? AND month = ?";
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, username);
                stmt.setString(2, dbMonthKey); // Use dbMonthKey here

                try (ResultSet rs = stmt.executeQuery()) {
                    Map<String, Double> spentMap = new HashMap<>();

                    for (Expense e : expenses) {
                        // ✅ FIX: Now checking against "2026-03" guaranteed
                        if (e.getDate() != null && e.getDate().startsWith(currentYearMonth)) {
                            spentMap.merge(e.getCategory(), e.getAmount(), Double::sum);
                        }
                    }

                    while (rs.next()) {
                        String category = rs.getString("category");
                        double amount = rs.getDouble("amount");
                        boolean edited = rs.getInt("edited") == 1;
                        String dateModified = rs.getString("date_modified"); 

                        rows.add(
                                new BudgetRow(
                                        category,
                                        amount,      // original
                                        amount,      // current
                                        edited,
                                        spentMap.getOrDefault(category, 0.0),
                                        dateModified 
                                )
                        );
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rows;
    }

   // =========================================
    // CHECK BUDGET WARNINGS (Database Version)
    // =========================================
    public List<String> checkBudgetWarnings(List<Expense> expenses) {
        List<String> warnings = new ArrayList<>();
        
        String dbMonthKey = MonthKey.current().toString(); 
        String currentYearMonth = java.time.YearMonth.now().toString(); // "2026-03"
        
        java.util.Map<String, Double> spent = new java.util.HashMap<>();

        for (Expense e : expenses) {
            // ✅ FIX: Use currentYearMonth for the date string comparison
            if (e.getDate() != null && e.getDate().startsWith(currentYearMonth)) {
                spent.merge(e.getCategory(), e.getAmount(), Double::sum);
            }
        }

        try (Connection localConn = DatabaseManager.connect()) {
            String sql = "SELECT category, amount FROM budgets WHERE username=? AND month=?";
            
            try (PreparedStatement stmt = localConn.prepareStatement(sql)) {
                stmt.setString(1, username);
                stmt.setString(2, dbMonthKey); // Use dbMonthKey here

                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        String category = rs.getString("category");
                        double limit = rs.getDouble("amount");
                        double used = spent.getOrDefault(category, 0.0);

                        if (used >= limit) {
                            warnings.add("❌ Budget exceeded for " + category);
                        } else if (used >= limit * 0.8) {
                            warnings.add("⚠ You are close to exceeding budget for " + category);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return warnings;
    }
}
