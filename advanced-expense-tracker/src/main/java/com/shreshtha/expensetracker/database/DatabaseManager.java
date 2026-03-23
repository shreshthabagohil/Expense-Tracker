package com.shreshtha.expensetracker.database;

import java.sql.*;

public class DatabaseManager {

    private static final String URL =
            "jdbc:sqlite:expense_tracker.db";

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public static void initializeDatabase() {

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {

            String createExpensesTable = """
                CREATE TABLE IF NOT EXISTS expenses (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT,
                    category TEXT NOT NULL,
                    amount REAL NOT NULL,
                    date TEXT NOT NULL,
                    mood TEXT,
                    description TEXT
                );
            """;

            stmt.execute(createExpensesTable);

            stmt.execute("""
             CREATE TABLE IF NOT EXISTS budgets (
                 id INTEGER PRIMARY KEY AUTOINCREMENT,
                 username TEXT,
                 month TEXT,
                 category TEXT,
                 amount REAL,
                 edited INTEGER DEFAULT 0,
                 date_modified TEXT, -- ✅ ADDED NEW COLUMN HERE
                 UNIQUE(username, month, category)
              );
            """);

            // ==========================================
            // NEW SAVINGS TABLES (Moved inside the try block!)
            // ==========================================

            // 1. Table to store the user's monthly savings goal and frequency
            String createSavingsGoalTable = """
                CREATE TABLE IF NOT EXISTS savings_goals (
                    username TEXT NOT NULL,
                    month TEXT NOT NULL,
                    target_amount REAL NOT NULL,
                    frequency TEXT DEFAULT 'Monthly',
                    PRIMARY KEY (username, month)
                );
            """;
            stmt.execute(createSavingsGoalTable);

            // 2. Table to store every deposit and withdrawal history
            String createSavingsHistoryTable = """
                CREATE TABLE IF NOT EXISTS savings_history (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT NOT NULL,
                    type TEXT NOT NULL,     -- 'DEPOSIT' or 'WITHDRAWAL'
                    amount REAL NOT NULL,
                    date TEXT NOT NULL,
                    description TEXT
                );
            """;
            stmt.execute(createSavingsHistoryTable);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
