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

public class BudgetView extends BorderPane 
{
    private final BudgetService budgetService;
    private final List<Expense> expenses;

    public BudgetView(
            BudgetService budgetService,
            List<Expense> expenses,
            Runnable onBack
    ) {
        this.budgetService = budgetService;
        this.expenses = expenses;

        setPadding(new Insets(20));

        Label title = new Label("Monthly Budget Overview");
        title.getStyleClass().add("title");

        TableView<BudgetRow> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<BudgetRow, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getCategory()
                )
        );

        TableColumn<BudgetRow, Number> budgetCol = new TableColumn<>("Budget");
        budgetCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleDoubleProperty(
                        data.getValue().getBudget()
                )
        );

        TableColumn<BudgetRow, Number> spentCol = new TableColumn<>("Spent");
        spentCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleDoubleProperty(
                        data.getValue().getSpent()
                )
        );

        TableColumn<BudgetRow, Number> remainingCol = new TableColumn<>("Remaining");
        remainingCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleDoubleProperty(
                        data.getValue().getRemaining()
                )
        );

        table.getColumns().addAll(categoryCol, budgetCol, spentCol, remainingCol);

        //Row highlighting logic 
        table.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(BudgetRow row, boolean empty) {
                super.updateItem(row, empty);

                if (row == null || empty) {
                    setStyle("");
                } else if (row.getRemaining() < 0) {
                    setStyle("-fx-background-color: #ffd6d6;");
                } else if (row.getRemaining() < row.getBudget() * 0.2) {
                    setStyle("-fx-background-color: #fff2cc;");
                } else {
                    setStyle("");
                }
            }
        });

        //loading data from service
        table.setItems(
                FXCollections.observableArrayList(
                        budgetService.getCurrentMonthBudgetRows(expenses)
                )
        );

         Button editBtn = new Button("Edit Selected Budget");
         editBtn.setOnAction(e -> {
         BudgetRow selected = table.getSelectionModel().getSelectedItem();
         if (selected == null) return;

          TextInputDialog dialog = new TextInputDialog(
             String.valueOf(selected.getBudget())
          );
          dialog.setTitle("Edit Budget");
          dialog.setHeaderText("Edit budget for " + selected.getCategory());
          dialog.setContentText("New limit:");
          dialog.showAndWait().ifPresent(value -> {
          try {
              double newLimit = Double.parseDouble(value);
              budgetService.updateBudget(selected.getCategory(), newLimit);
              table.setItems(
                 FXCollections.observableArrayList(
                     budgetService.getCurrentMonthBudgetRows(expenses)
                 )
              );
          } catch (Exception ex) {
              new Alert(Alert.AlertType.ERROR, ex.getMessage()).show();
            }
         });
        });

        Button backBtn = new Button("Back");
        backBtn.setOnAction(e -> onBack.run());


        VBox topBox = new VBox(10, title);
        VBox bottomBox = new VBox(10, editBtn, backBtn);

        setTop(topBox);
        setCenter(table);
        setBottom(bottomBox);
    }
}
