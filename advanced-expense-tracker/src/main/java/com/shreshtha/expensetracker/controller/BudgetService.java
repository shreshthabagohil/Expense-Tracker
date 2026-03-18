package com.shreshtha.expensetracker.controller;

import com.shreshtha.expensetracker.database.BudgetRepository;
import com.shreshtha.expensetracker.database.DatabaseManager;
import com.shreshtha.expensetracker.model.*;


import java.sql.*;
import java.util.*;

public class BudgetService {

    private Connection conn;
    private String username;
    private BudgetRepository budgetRepo;

    public BudgetService() {

    try {

        conn = DatabaseManager.connect();
        budgetRepo = new BudgetRepository(conn);

    } catch (SQLException e) {
        e.printStackTrace();
    }
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
            String checkSql =
                    "SELECT amount FROM budgets WHERE username = ? AND month = ? AND category = ?";

            PreparedStatement checkStmt =
                    conn.prepareStatement(checkSql);

            checkStmt.setString(1, username);
            checkStmt.setString(2, month);
            checkStmt.setString(3, category);

            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                throw new IllegalStateException(
                        "Budget already set for this category."
                );
            }

            String insertSql =
                    "INSERT INTO budgets(username, month, category, amount, edited) VALUES (?, ?, ?, ?, 0)";

            PreparedStatement insertStmt =
                    conn.prepareStatement(insertSql);

            insertStmt.setString(1, username);
            insertStmt.setString(2, month);
            insertStmt.setString(3, category);
            insertStmt.setDouble(4, limit);

            insertStmt.executeUpdate();

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

            String checkSql =
                    "SELECT edited FROM budgets WHERE username = ? AND month = ? AND category = ?";

            PreparedStatement checkStmt =
                    conn.prepareStatement(checkSql);

            checkStmt.setString(1, username);
            checkStmt.setString(2, month);
            checkStmt.setString(3, category);

            ResultSet rs = checkStmt.executeQuery();

            if (!rs.next()) {
                throw new IllegalStateException("No budget for category.");
            }

            boolean alreadyEdited = rs.getInt("edited") == 1;

            if (alreadyEdited) {
                throw new IllegalStateException(
                        "Budget can only be edited once per month."
                );
            }

            String updateSql =
                    "UPDATE budgets SET amount = ?, edited = 1 WHERE username = ? AND month = ? AND category = ?";

            PreparedStatement updateStmt =
                    conn.prepareStatement(updateSql);

            updateStmt.setDouble(1, newLimit);
            updateStmt.setString(2, username);
            updateStmt.setString(3, month);
            updateStmt.setString(4, category);

            updateStmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ===============================
    // GET BUDGET ROWS FOR UI
    // ===============================

    public List<BudgetRow> getCurrentMonthBudgetRows(
            List<Expense> expenses) {

        List<BudgetRow> rows = new ArrayList<>();

        String month = MonthKey.current().toString();

        try (Connection conn = DatabaseManager.connect()) {

            String sql =
                    "SELECT category, amount, edited FROM budgets WHERE username = ? AND month = ?";

            PreparedStatement stmt =
                    conn.prepareStatement(sql);

            stmt.setString(1, username);
            stmt.setString(2, month);

            ResultSet rs = stmt.executeQuery();

            Map<String, Double> spentMap = new HashMap<>();

            for (Expense e : expenses) {
                spentMap.merge(
                        e.getCategory(),
                        e.getAmount(),
                        Double::sum
                );
            }

            while (rs.next()) {

                String category = rs.getString("category");
                double amount = rs.getDouble("amount");
                boolean edited = rs.getInt("edited") == 1;

                rows.add(
                        new BudgetRow(
                                category,
                                amount,      // original
                                amount,      // current
                                edited,
                                spentMap.getOrDefault(category, 0.0)
                        )
                );
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
        String currentMonth = java.time.YearMonth.now().toString();
        Map<String, Double> spent = new HashMap<>();

        for (Expense e : expenses) {
            spent.merge(e.getCategory(), e.getAmount(), Double::sum);
        }

        // FIX: Using try-with-resources to automatically close the connection and prevent DB locks!
        try (Connection localConn = DatabaseManager.connect()) {
            String sql = "SELECT category, amount FROM budgets WHERE username=? AND month=?";
            
            try (PreparedStatement stmt = localConn.prepareStatement(sql)) {
                stmt.setString(1, username);
                stmt.setString(2, currentMonth);

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
