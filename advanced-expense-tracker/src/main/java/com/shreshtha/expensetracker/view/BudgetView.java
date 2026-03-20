package com.shreshtha.expensetracker.view;

import com.shreshtha.expensetracker.controller.BudgetService;
import com.shreshtha.expensetracker.model.BudgetRow;
import com.shreshtha.expensetracker.model.Expense;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;

public class BudgetView extends BorderPane {

    private final BudgetService budgetService;
    private final List<Expense> expenses;

    private final TableView<BudgetRow> table = new TableView<>();
    private final VBox topContainer = new VBox(20);

    private final ComboBox<String> categoryBox = new ComboBox<>();
    private final TextField amountField = new TextField();

    public BudgetView(
            BudgetService budgetService,
            List<Expense> expenses,
            Runnable onBack
    ) {

        this.budgetService = budgetService;
        this.expenses = expenses;

        setPadding(new Insets(20));

        // ============================
        // TITLE
        // ============================
        Label title = new Label("Monthly Budget Overview");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // ============================
        // SET BUDGET SECTION
        // ============================
        categoryBox.getItems().addAll(
                "Food", "Transport", "Shopping",
                "Bills", "Entertainment"
        );
        categoryBox.setPromptText("Select Category");
        amountField.setPromptText("Enter Budget Amount");

        Button setBtn = new Button("Set Budget");
        setBtn.setOnAction(e -> {
            String category = categoryBox.getValue();
            if (category == null || amountField.getText().isEmpty()) {
                showAlert("Please fill all fields");
                return;
            }

            try {
                double amount = Double.parseDouble(amountField.getText());
                budgetService.setBudgetOnce(category, amount);
                amountField.clear();
                categoryBox.setValue(null);
                refresh();
                showAlert("Budget saved successfully");

            } catch (Exception ex) {
                showAlert(ex.getMessage());
            }
        });

        HBox setSection = new HBox(15, categoryBox, amountField, setBtn);
        setSection.setAlignment(Pos.CENTER_LEFT);

        // ============================
        // TABLE SECTION
        // ============================
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<BudgetRow, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCategory()));

        TableColumn<BudgetRow, Number> originalCol = new TableColumn<>("Original");
        originalCol.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getOriginalBudget()));

        TableColumn<BudgetRow, Number> currentCol = new TableColumn<>("Current");
        currentCol.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getCurrentBudget()));

        TableColumn<BudgetRow, Number> spentCol = new TableColumn<>("Spent");
        spentCol.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getSpent()));

        TableColumn<BudgetRow, Number> remainingCol = new TableColumn<>("Remaining");
        remainingCol.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getRemaining()));

        // Progress Column
        TableColumn<BudgetRow, Double> progressCol = new TableColumn<>("Usage");
        progressCol.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getProgress()).asObject());
        progressCol.setCellFactory(col -> new TableCell<>() {
            private final ProgressBar bar = new ProgressBar();
            @Override
            protected void updateItem(Double value, boolean empty) {
                super.updateItem(value, empty);
                if (empty || value == null) {
                    setGraphic(null);
                } else {
                    bar.setProgress(value);
                    if (value >= 1.0) {
                        bar.setStyle("-fx-accent: #E57373;");
                    } else if (value >= 0.8) {
                        bar.setStyle("-fx-accent: #FFD54F;");
                    } else {
                        bar.setStyle("-fx-accent: #81C784;");
                    }
                    setGraphic(bar);
                }
            }
        });

        TableColumn<BudgetRow, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStatus()));

        // ✅ ADDED NEW DATE COLUMN
        TableColumn<BudgetRow, String> dateCol = new TableColumn<>("Date Set/Edited");
        dateCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDateModified()));

        // ✅ Added dateCol to the end of this list
        table.getColumns().addAll(
                categoryCol,
                originalCol,
                currentCol,
                spentCol,
                remainingCol,
                progressCol,
                statusCol,
                dateCol 
        );

        // ============================
        // EDIT SECTION
        // ============================
        Button editBtn = new Button("Edit Selected");
        editBtn.setOnAction(e -> {
            BudgetRow selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert("Select a category first");
                return;
            }

            if (selected.isEdited()) {
                showAlert("Budget can only be edited once per month");
                return;
            }

            TextInputDialog dialog = new TextInputDialog(String.valueOf(selected.getCurrentBudget()));
            dialog.setTitle("Edit Budget");
            dialog.setHeaderText("Edit budget for " + selected.getCategory());
            dialog.setContentText("New limit:");

            dialog.showAndWait().ifPresent(value -> {
                try {
                    double newLimit = Double.parseDouble(value);
                    budgetService.updateBudget(selected.getCategory(), newLimit);
                    refresh();
                } catch (Exception ex) {
                    showAlert(ex.getMessage());
                }
            });
        });

        Button backBtn = new Button("Back");
        backBtn.setOnAction(e -> onBack.run());

        VBox bottom = new VBox(10, editBtn, backBtn);
        bottom.setPadding(new Insets(10));

        // ============================
        // LAYOUT STRUCTURE
        // ============================
        topContainer.getChildren().addAll(title, setSection);
        setTop(topContainer);
        setCenter(table);
        setBottom(bottom);

        refresh();
    }

    // ============================
    // ANALYTICS PANEL
    // ============================
    private HBox createAnalyticsPanel(List<BudgetRow> rows) {
        double totalBudget = 0;
        double totalSpent = 0;

        for (BudgetRow row : rows) {
            totalBudget += row.getCurrentBudget();
            totalSpent += row.getSpent();
        }

        double remaining = totalBudget - totalSpent;
        double usagePercent = totalBudget == 0 ? 0 : (totalSpent / totalBudget) * 100;

        Label totalBudgetLbl = new Label("Total Budget: ₹ " + totalBudget);
        Label totalSpentLbl = new Label("Total Spent: ₹ " + totalSpent);
        Label remainingLbl = new Label("Remaining: ₹ " + remaining);
        Label percentLbl = new Label(String.format("Usage: %.2f %%", usagePercent));

        if (usagePercent > 100) {
            percentLbl.setStyle("-fx-text-fill: red;");
        } else if (usagePercent > 80) {
            percentLbl.setStyle("-fx-text-fill: orange;");
        } else {
            percentLbl.setStyle("-fx-text-fill: green;");
        }

        HBox box = new HBox(30, totalBudgetLbl, totalSpentLbl, remainingLbl, percentLbl);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(10));

        return box;
    }

    private void refresh() {
        List<BudgetRow> rows = budgetService.getCurrentMonthBudgetRows(expenses);
        table.setItems(FXCollections.observableArrayList(rows));

        HBox analytics = createAnalyticsPanel(rows);

        if (topContainer.getChildren().size() > 2) {
            topContainer.getChildren().remove(2);
        }

        topContainer.getChildren().add(analytics);
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Budget");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
