package com.shreshtha.expensetracker.database;

import com.shreshtha.expensetracker.model.SavingsGoal;
import com.shreshtha.expensetracker.model.SavingsTransaction;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SavingsRepository {

    // ===============================
    // SET OR UPDATE SAVINGS GOAL
    // ===============================
    public void setSavingsGoal(String username, String month, double targetAmount, String frequency) {
        String sql = """
            INSERT INTO savings_goals (username, month, target_amount, frequency) 
            VALUES (?, ?, ?, ?)
            ON CONFLICT(username, month) DO UPDATE SET 
            target_amount = excluded.target_amount, 
            frequency = excluded.frequency
        """;

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, month);
            stmt.setDouble(3, targetAmount);
            stmt.setString(4, frequency);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ===============================
    // GET CURRENT SAVINGS GOAL
    // ===============================
    public SavingsGoal getSavingsGoal(String username, String month) {
        String sql = "SELECT target_amount, frequency FROM savings_goals WHERE username = ? AND month = ?";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, month);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new SavingsGoal(username, month, rs.getDouble("target_amount"), rs.getString("frequency"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // No goal set yet
    }

    // ===============================
    // ADD DEPOSIT OR WITHDRAWAL
    // ===============================
    public void addTransaction(String username, String type, double amount, String date, String description) {
        String sql = "INSERT INTO savings_history (username, type, amount, date, description) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, type); // "DEPOSIT" or "WITHDRAWAL"
            stmt.setDouble(3, amount);
            stmt.setString(4, date);
            stmt.setString(5, description);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ===============================
    // GET TOTAL CURRENT SAVINGS (Math!)
    // ===============================
    public double getTotalSavings(String username) {
        double total = 0.0;
        String sql = "SELECT type, amount FROM savings_history WHERE username = ?";
        
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String type = rs.getString("type");
                    double amount = rs.getDouble("amount");
                    
                    if ("DEPOSIT".equals(type)) {
                        total += amount;
                    } else if ("WITHDRAWAL".equals(type)) {
                        total -= amount;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return total;
    }

    // ===============================
    // GET TRANSACTION HISTORY
    // ===============================
    public List<SavingsTransaction> getTransactionHistory(String username) {
        List<SavingsTransaction> list = new ArrayList<>();
        String sql = "SELECT id, type, amount, date, description FROM savings_history WHERE username = ? ORDER BY id DESC";
        
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new SavingsTransaction(
                            rs.getInt("id"),
                            username,
                            rs.getString("type"),
                            rs.getDouble("amount"),
                            rs.getString("date"),
                            rs.getString("description")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
