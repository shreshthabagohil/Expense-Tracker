package com.shreshtha.expensetracker.database;

import com.shreshtha.expensetracker.model.Expense;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExpenseRepository {

    private String currentUser;

    public void setUser(String username) {
        this.currentUser = username;
    }

    public void addExpense(Expense expense) {

        String sql = "INSERT INTO expenses(user, category, amount, date, description) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, currentUser);
            stmt.setString(2, expense.getCategory());
            stmt.setDouble(3, expense.getAmount());
            stmt.setString(4, expense.getDate());
            stmt.setString(5, expense.getDescription());

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Expense> getAllExpenses() {

        List<Expense> expenses = new ArrayList<>();

        String sql = "SELECT * FROM expenses WHERE user = ?";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, currentUser);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {

                Expense expense = new Expense(
                        rs.getString("category"),
                        rs.getDouble("amount"),
                        rs.getString("date"),
                        rs.getString("description")
                );

                expenses.add(expense);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return expenses;
    }

    public void deleteExpense(Expense expense) {

        String sql = "DELETE FROM expenses WHERE user = ? AND category = ? AND amount = ? AND date = ?";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, currentUser);
            stmt.setString(2, expense.getCategory());
            stmt.setDouble(3, expense.getAmount());
            stmt.setString(4, expense.getDate());

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getHighestCategory() {

        String sql = """
                SELECT category, SUM(amount) as total
                FROM expenses
                WHERE user = ?
                GROUP BY category
                ORDER BY total DESC
                LIMIT 1
                """;

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, currentUser);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("category");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "No data";
    }
}
