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
             UNIQUE(username, month, category)
              );
            """);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
