package com.shreshtha.expensetracker.view;

import com.shreshtha.expensetracker.controller.BudgetService;
import com.shreshtha.expensetracker.database.DatabaseManager;
import com.shreshtha.expensetracker.database.ExpenseRepository;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainDashboard extends Application {

    private BorderPane root;
    private ExpenseRepository repo;
    private BudgetService budgetService;

    private DashBoardView dashboardView;
    private String username;

    @Override
    public void start(Stage stage) {

        // Initialize DB
        DatabaseManager.initializeDatabase();

        // Login Dialog
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Login");
        dialog.setHeaderText("Welcome to Expense Tracker");
        dialog.setContentText("Enter username:");
        dialog.getDialogPane().setGraphic(null);

        username = dialog.showAndWait().orElse("");
        if (username.isBlank()) {
            System.exit(0);
        }

        // Initialize repo and services
        repo = new ExpenseRepository();
        budgetService = new BudgetService();

        repo.setUser(username);
        budgetService.setUser(username);

        // Sidebar buttons
        Button dashboardBtn = sideButton("Dashboard", "/icons/dashboard.png");
        Button addExpenseBtn = sideButton("Add Expense", "/icons/save.png");
        Button viewExpenseBtn = sideButton("View Expenses", "/icons/refresh.png");
        Button budgetBtn = sideButton("Set Budget", "/icons/setbudget.png");
        Button insightsBtn = sideButton("Insights", "/icons/dashboard.png");
        Button exitBtn = sideButton("Exit", "/icons/exit.png");

        VBox sidebar = new VBox(
                dashboardBtn,
                addExpenseBtn,
                viewExpenseBtn,
                budgetBtn,
                insightsBtn,
                exitBtn
        );

        sidebar.setSpacing(12);
        sidebar.setPadding(new Insets(20));
        sidebar.getStyleClass().add("sidebar");

        // Create Dashboard view
        dashboardView = new DashBoardView(username, repo);

        root = new BorderPane();
        root.setLeft(sidebar);
        root.setCenter(dashboardView);

        // =========================
        // NAVIGATION HANDLERS
        // =========================

        dashboardBtn.setOnAction(e -> {
            dashboardView.refresh();
            root.setCenter(dashboardView);
        });

        addExpenseBtn.setOnAction(e ->
                root.setCenter(new AddExpenseView(
                        () -> {
                            dashboardView.refresh();
                            root.setCenter(dashboardView);
                        },
                        repo,
                        budgetService
                ))
        );

        viewExpenseBtn.setOnAction(e ->
                root.setCenter(new ViewExpenseView(repo))
        );

        budgetBtn.setOnAction(e ->
                root.setCenter(new BudgetView(
                        budgetService,
                        repo.getAllExpenses(),
                        () -> {
                            dashboardView.refresh();
                            root.setCenter(dashboardView);
                        }
                ))
        );

        insightsBtn.setOnAction(e -> {
            InsightsView insightsView =
                    new InsightsView(repo, () -> {
                        dashboardView.refresh();
                        root.setCenter(dashboardView);
                    });
            root.setCenter(insightsView);
        });

        exitBtn.setOnAction(e -> stage.close());

        // Scene
        Scene scene = new Scene(root, 900, 600);
        scene.getStylesheets().add(
                getClass().getResource("/theme.css").toExternalForm()
        );

        stage.setTitle("Expense Tracker");
        stage.setScene(scene);
        stage.show();
    }

    private Button sideButton(String text, String iconPath) {

        ImageView icon = new ImageView(
                new Image(getClass().getResourceAsStream(iconPath))
        );
        icon.setFitWidth(16);
        icon.setFitHeight(16);

        Button btn = new Button(text, icon);
        btn.setContentDisplay(javafx.scene.control.ContentDisplay.LEFT);
        btn.getStyleClass().add("sidebar-button");
        btn.setPrefWidth(170);
        btn.setPrefHeight(40);

        return btn;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
