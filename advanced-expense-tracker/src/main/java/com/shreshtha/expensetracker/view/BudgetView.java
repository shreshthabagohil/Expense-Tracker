package com.shreshtha.expensetracker.view;

import com.shreshtha.expensetracker.controller.BudgetService;
import com.shreshtha.expensetracker.model.BudgetRow;
import com.shreshtha.expensetracker.model.Expense;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.util.List;

public class BudgetView extends BorderPane {

    private final BudgetService budgetService;
    private final List<Expense> expenses;
    private final TableView<BudgetRow> table = new TableView<>();

    public BudgetView(
            BudgetService budgetService,
            List<Expense> expenses,
            Runnable onBack
    ) {

        this.budgetService = budgetService;
        this.expenses = expenses;

        setPadding(new Insets(20));

        Label title = new Label("Monthly Budget Overview");

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Category
        TableColumn<BudgetRow, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getCategory()
                )
        );

        // Original Budget
        TableColumn<BudgetRow, Number> originalCol = new TableColumn<>("Original");
        originalCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleDoubleProperty(
                        data.getValue().getOriginalBudget()
                )
        );

        // Current Budget
        TableColumn<BudgetRow, Number> currentCol = new TableColumn<>("Current");
        currentCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleDoubleProperty(
                        data.getValue().getCurrentBudget()
                )
        );

        // Spent
        TableColumn<BudgetRow, Number> spentCol = new TableColumn<>("Spent");
        spentCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleDoubleProperty(
                        data.getValue().getSpent()
                )
        );

        // Remaining
        TableColumn<BudgetRow, Number> remainingCol = new TableColumn<>("Remaining");
        remainingCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleDoubleProperty(
                        data.getValue().getRemaining()
                )
        );

        // Status
        TableColumn<BudgetRow, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getStatus()
                )
        );

        table.getColumns().addAll(
                categoryCol,
                originalCol,
                currentCol,
                spentCol,
                remainingCol,
                statusCol
        );

        // Row highlight based on CURRENT budget
        table.setRowFactory(tv -> new TableRow<>() {
          @Override
          protected void updateItem(BudgetRow row, boolean empty) {
          super.updateItem(row, empty);

          if (row == null || empty) {
             setStyle("");
          }
          else if (row.getRemaining() < 0) {
             setStyle("-fx-background-color: #ffd6d6;"); // exceeded
         }
         else if (row.getRemaining() < row.getCurrentBudget() * 0.2) {
             setStyle("-fx-background-color: #fff2cc;"); // 80% used
         }
         else {
             setStyle("");
         }
        }
       });


       // --- SET BUDGET SECTION ---

        TextField categoryField = new TextField();
        categoryField.setPromptText("Category");

        TextField limitField = new TextField();
        limitField.setPromptText("Monthly Limit");

        Button setBtn = new Button("Set Budget");

        setBtn.setOnAction(e -> {
         try {
         String category = categoryField.getText();
         double limit = Double.parseDouble(limitField.getText());

         if (category.isBlank()) {
             throw new IllegalArgumentException("Category required");
         }

         budgetService.setBudgetOnce(category, limit);

         categoryField.clear();
         limitField.clear();

          refresh();

         new Alert(Alert.AlertType.INFORMATION,
                 "Budget set successfully!"
          ).show();

       } catch (Exception ex) {
         new Alert(Alert.AlertType.ERROR,
                 ex.getMessage()
          ).show();
       }
     });


        refresh();

        // Edit button
        Button editBtn = new Button("Edit Selected");

        editBtn.setOnAction(e -> {

            BudgetRow selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) return;

            if (selected.isEdited()) {
                new Alert(Alert.AlertType.INFORMATION,
                        "Budget can only be edited once per month"
                ).show();
                return;
            }

            TextInputDialog dialog = new TextInputDialog(
                    String.valueOf(selected.getCurrentBudget())
            );

            dialog.setTitle("Edit Budget");
            dialog.setHeaderText("Edit budget for " + selected.getCategory());
            dialog.setContentText("New limit:");

            dialog.showAndWait().ifPresent(value -> {
                try {
                    double newLimit = Double.parseDouble(value);

                    budgetService.updateBudget(
                            selected.getCategory(),
                            newLimit
                    );

                    refresh();

                } catch (Exception ex) {
                    new Alert(Alert.AlertType.ERROR,
                            ex.getMessage()
                    ).show();
                }
            });
        });

        Button backBtn = new Button("Back");
        backBtn.setOnAction(e -> onBack.run());

        VBox top = new VBox(10, title);
        package com.shreshtha.expensetracker.view;

import com.shreshtha.expensetracker.controller.BudgetService;
import com.shreshtha.expensetracker.model.BudgetRow;
import com.shreshtha.expensetracker.model.Expense;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.util.List;

public class BudgetView extends BorderPane {

    private final BudgetService budgetService;
    private final List<Expense> expenses;
    private final TableView<BudgetRow> table = new TableView<>();

    public BudgetView(
            BudgetService budgetService,
            List<Expense> expenses,
            Runnable onBack
    ) {

        this.budgetService = budgetService;
        this.expenses = expenses;

        setPadding(new Insets(20));

        Label title = new Label("Monthly Budget Overview");

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Category
        TableColumn<BudgetRow, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getCategory()
                )
        );

        // Original Budget
        TableColumn<BudgetRow, Number> originalCol = new TableColumn<>("Original");
        originalCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleDoubleProperty(
                        data.getValue().getOriginalBudget()
                )
        );

        // Current Budget
        TableColumn<BudgetRow, Number> currentCol = new TableColumn<>("Current");
        currentCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleDoubleProperty(
                        data.getValue().getCurrentBudget()
                )
        );

        // Spent
        TableColumn<BudgetRow, Number> spentCol = new TableColumn<>("Spent");
        spentCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleDoubleProperty(
                        data.getValue().getSpent()
                )
        );

        // Remaining
        TableColumn<BudgetRow, Number> remainingCol = new TableColumn<>("Remaining");
        remainingCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleDoubleProperty(
                        data.getValue().getRemaining()
                )
        );

        // Status
        TableColumn<BudgetRow, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getStatus()
                )
        );

        table.getColumns().addAll(
                categoryCol,
                originalCol,
                currentCol,
                spentCol,
                remainingCol,
                statusCol
        );

        // Row highlight based on CURRENT budget
        table.setRowFactory(tv -> new TableRow<>() {
          @Override
          protected void updateItem(BudgetRow row, boolean empty) {
          super.updateItem(row, empty);

          if (row == null || empty) {
             setStyle("");
          }
          else if (row.getRemaining() < 0) {
             setStyle("-fx-background-color: #ffd6d6;"); // exceeded
         }
         else if (row.getRemaining() < row.getCurrentBudget() * 0.2) {
             setStyle("-fx-background-color: #fff2cc;"); // 80% used
         }
         else {
             setStyle("");
         }
        }
       });


       // --- SET BUDGET SECTION ---

        TextField categoryField = new TextField();
        categoryField.setPromptText("Category");

        TextField limitField = new TextField();
        limitField.setPromptText("Monthly Limit");

        Button setBtn = new Button("Set Budget");

        setBtn.setOnAction(e -> {
         try {
         String category = categoryField.getText();
         double limit = Double.parseDouble(limitField.getText());

         if (category.isBlank()) {
             throw new IllegalArgumentException("Category required");
         }

         budgetService.setBudgetOnce(category, limit);

         categoryField.clear();
         limitField.clear();

          refresh();

         new Alert(Alert.AlertType.INFORMATION,
                 "Budget set successfully!"
          ).show();

       } catch (Exception ex) {
         new Alert(Alert.AlertType.ERROR,
                 ex.getMessage()
          ).show();
       }
     });


        refresh();

        // Edit button
        Button editBtn = new Button("Edit Selected");

        editBtn.setOnAction(e -> {

            BudgetRow selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) return;

            if (selected.isEdited()) {
                new Alert(Alert.AlertType.INFORMATION,
                        "Budget can only be edited once per month"
                ).show();
                return;
            }

            TextInputDialog dialog = new TextInputDialog(
                    String.valueOf(selected.getCurrentBudget())
            );

            dialog.setTitle("Edit Budget");
            dialog.setHeaderText("Edit budget for " + selected.getCategory());
            dialog.setContentText("New limit:");

            dialog.showAndWait().ifPresent(value -> {
                try {
                    double newLimit = Double.parseDouble(value);

                    budgetService.updateBudget(
                            selected.getCategory(),
                            newLimit
                    );

                    refresh();

                } catch (Exception ex) {
                    new Alert(Alert.AlertType.ERROR,
                            ex.getMessage()
                    ).show();
                }
            });
        });

        Button backBtn = new Button("Back");
        backBtn.setOnAction(e -> onBack.run());

        VBox top = new VBox(10, title);
        VBox bottom = new VBox(
        10,
        categoryField,
        limitField,
        setBtn,
        editBtn,
        backBtn);


        setTop(top);
        setCenter(table);
        setBottom(bottom);
    }

    private void refresh() {
        table.setItems(
                FXCollections.observableArrayList(
                        budgetService.getCurrentMonthBudgetRows(expenses)
                )
        );
    }
}


        setTop(top);
        setCenter(table);
        setBottom(bottom);
    }

    private void refresh() {
        table.setItems(
                FXCollections.observableArrayList(
                        budgetService.getCurrentMonthBudgetRows(expenses)
                )
        );
    }
}
