package com.shreshtha.expensetracker.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    private static final String URL = "jdbc:sqlite:expense_tracker.db";

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public static void initializeDatabase() {
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {

                stmt.execute("""
    CREATE TABLE IF NOT EXISTS expenses (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        user TEXT,
        category TEXT,
        amount REAL,
        date TEXT,
        description TEXT
    )
""");

            String createExpensesTable = """
                CREATE TABLE IF NOT EXISTS expenses (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    category TEXT NOT NULL,
                    amount REAL NOT NULL,
                    date TEXT NOT NULL,
                    description TEXT
                );
            """;

            String createBudgetTable = """
                CREATE TABLE IF NOT EXISTS budgets (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    category TEXT UNIQUE NOT NULL,
                    original REAL NOT NULL,
                    current REAL NOT NULL,
                    edited INTEGER DEFAULT 0
                );
            """;

            stmt.execute(createExpensesTable);
            stmt.execute(createBudgetTable);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
