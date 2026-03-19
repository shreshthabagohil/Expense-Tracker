package com.shreshtha.expensetracker.database;

import com.shreshtha.expensetracker.model.Expense;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExpenseRepository {

    private String username;

    public void setUser(String username) {
        this.username = username;
    }

    // ===============================
    // ADD EXPENSE
    // ===============================
    public void addExpense(Expense e) {
        String sql = """
            INSERT INTO expenses
            (username, category, amount, date, mood, description)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, e.getCategory());
            stmt.setDouble(3, e.getAmount());
            stmt.setString(4, e.getDate());
            stmt.setString(5, e.getMood());
            stmt.setString(6, e.getDescription());

            stmt.executeUpdate();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // ===============================
    // GET ALL EXPENSES
    // ===============================
    public List<Expense> getAllExpenses() {
        List<Expense> list = new ArrayList<>();
        String sql = """
            SELECT id, category, amount, date, mood, description
            FROM expenses
            WHERE username = ?
            ORDER BY id DESC
        """;

        // Bulletproof try-with-resources to prevent locks
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Expense e = new Expense(
                            rs.getInt("id"),
                            rs.getDouble("amount"),
                            rs.getString("category"),
                            rs.getString("date"),
                            rs.getString("mood"),
                            rs.getString("description")
                    );
                    list.add(e);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // ===============================
    // DELETE EXPENSE
    // ===============================
    public boolean deleteExpense(Expense e) {
        String sql = "DELETE FROM expenses WHERE id=?";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, e.getId());
            int rows = stmt.executeUpdate();

            if (rows == 0) {
                System.out.println("⚠️ Delete failed: No expense found with ID " + e.getId());
                return false;
            }
            
            System.out.println("✅ Successfully deleted expense ID " + e.getId());
            return true; 

        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    // ===============================
    // INSIGHTS HELPERS
    // ===============================
    public String getHighestCategory() {
        return getAllExpenses().stream()
                .reduce((e1, e2) -> e1.getAmount() > e2.getAmount() ? e1 : e2)
                .map(Expense::getCategory)
                .orElse("N/A");
    }

    public String getHighestMood() {
        return getAllExpenses().stream()
                .reduce((e1, e2) -> e1.getAmount() > e2.getAmount() ? e1 : e2)
                .map(Expense::getMood)
                .orElse("N/A");
    }
}
