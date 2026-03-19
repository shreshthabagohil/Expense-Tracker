package com.shreshtha.expensetracker.view;

import com.shreshtha.expensetracker.database.ExpenseRepository;
import com.shreshtha.expensetracker.model.Expense;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ViewExpenseView extends VBox {

    private final TableView<Expense> table = new TableView<>();
    private final ExpenseRepository repo;

    public ViewExpenseView(ExpenseRepository repo) {
        this.repo = repo;

        setPadding(new Insets(25));
        setSpacing(18);

        Label title = new Label("View Expenses");
        title.getStyleClass().add("page-title");

        setupTable(); 
        loadData();

        Button refreshBtn = iconButton("Refresh", "/icons/refresh.png");
        refreshBtn.getStyleClass().add("primary-btn");
        refreshBtn.setOnAction(e -> loadData());

        Button deleteBtn = iconButton("Delete Selected", "/icons/delete.png");
        deleteBtn.getStyleClass().add("danger-btn");
        deleteBtn.disableProperty().bind(
                table.getSelectionModel().selectedItemProperty().isNull()
        );

        deleteBtn.setOnAction(e -> deleteSelected());

        HBox actions = new HBox(16, refreshBtn, deleteBtn);
        actions.setAlignment(Pos.CENTER_LEFT);
        actions.setPadding(new Insets(10, 0, 0, 0));

        getChildren().addAll(title, table, actions);
    }

    private void loadData(){
        ObservableList<Expense> data =
                FXCollections.observableArrayList(
                        repo.getAllExpenses()
                );
        table.setItems(data);
    }

    private void setupTable() {
        TableColumn<Expense, Double> amount = new TableColumn<>("Amount");
        amount.setCellValueFactory(d ->
                new javafx.beans.property.SimpleDoubleProperty(
                        d.getValue().getAmount()).asObject()
        );

        TableColumn<Expense, String> category = new TableColumn<>("Category");
        category.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(
                        d.getValue().getCategory())
        );

        TableColumn<Expense, String> date = new TableColumn<>("Date");
        date.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(
                        d.getValue().getDate())
        );

        TableColumn<Expense, String> mood = new TableColumn<>("Mood");
        mood.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(
                        d.getValue().getMood())
        );

        table.getColumns().addAll(amount, category, date, mood);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

   private void deleteSelected() {
        Expense selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete Expense?");
        confirm.setContentText("Are you sure you want to delete this " + selected.getCategory() + " expense?");

        // Add your CSS (make sure the path is correct for your project)
        try {
            confirm.getDialogPane().getStylesheets().add(
                getClass().getResource("/theme.css").toExternalForm()
            );
        } catch (Exception e) {
            System.out.println("Note: theme.css not found, skipping styling for dialog.");
        }

        confirm.showAndWait().ifPresent(res -> {
            if (res == ButtonType.OK) {
                
                System.out.println("Attempting to delete ID: " + selected.getId() + " Category: " + selected.getCategory());
                
                boolean success = repo.deleteExpense(selected);
                
                if (success) {
                    table.getItems().remove(selected);
                    table.getSelectionModel().clearSelection();
                } else {
                    Alert error = new Alert(Alert.AlertType.ERROR);
                    error.setContentText("Failed to delete! Check your terminal/console for details.");
                    error.showAndWait();
                }
            }
        });
    }

    private Button iconButton(String text, String iconPath) {
        ImageView icon = new ImageView(
                new Image(getClass().getResourceAsStream(iconPath))
        );
        icon.setFitWidth(16);
        icon.setFitHeight(16);

        Button btn = new Button(text, icon);
        btn.setContentDisplay(ContentDisplay.LEFT);
        btn.setPrefHeight(38);
        btn.setStyle(
                "-fx-background-radius: 10;" +
                "-fx-padding: 8 16;"
        );
        return btn;
    }
}
