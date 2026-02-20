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

    // Set Budget UI components
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

        Label title = new Label("Monthly Budget Overview");
        title.getStyleClass().add("page-title");

        // =====================================
        // SET BUDGET SECTION
        // =====================================

        categoryBox.getItems().addAll(
                "Food",
                "Transport",
                "Shopping",
                "Bills",
                "Entertainment"
        );

        categoryBox.setPromptText("Select Category");
        amountField.setPromptText("Enter Budget Amount");

        Button setBtn = new Button("Set Budget");

        setBtn.setOnAction(e -> {

            String category = categoryBox.getValue();

            if (category == null || amountField.getText().isEmpty()) {
                new Alert(Alert.AlertType.WARNING,
                        "Please fill all fields").show();
                return;
            }

            try {

                double amount =
                        Double.parseDouble(amountField.getText());

                budgetService.setBudgetOnce(category, amount);

                refresh();

                amountField.clear();
                categoryBox.setValue(null);

            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR,
                        ex.getMessage()).show();
            }
        });

        VBox setSection = new VBox(10,
                new Label("Set Monthly Budget"),
                categoryBox,
                amountField,
                setBtn
        );

        // =====================================
        // TABLE SECTION
        // =====================================

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Category
        TableColumn<BudgetRow, String> categoryCol =
                new TableColumn<>("Category");
        categoryCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getCategory()
                )
        );

        // Original
        TableColumn<BudgetRow, Number> originalCol =
                new TableColumn<>("Original");
        originalCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleDoubleProperty(
                        data.getValue().getOriginalBudget()
                )
        );

        // Current
        TableColumn<BudgetRow, Number> currentCol =
                new TableColumn<>("Current");
        currentCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleDoubleProperty(
                        data.getValue().getCurrentBudget()
                )
        );

        // Spent
        TableColumn<BudgetRow, Number> spentCol =
                new TableColumn<>("Spent");
        spentCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleDoubleProperty(
                        data.getValue().getSpent()
                )
        );

        // Remaining
        TableColumn<BudgetRow, Number> remainingCol =
                new TableColumn<>("Remaining");
        remainingCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleDoubleProperty(
                        data.getValue().getRemaining()
                )
        );

        // Progress Column
TableColumn<BudgetRow, Double> progressCol = new TableColumn<>("Usage");

progressCol.setCellValueFactory(data ->
        new javafx.beans.property.SimpleDoubleProperty(
                data.getValue().getProgress()
        ).asObject()
);

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
                bar.setStyle("-fx-accent: #E57373;"); // red
            } else if (value >= 0.8) {
                bar.setStyle("-fx-accent: #FFD54F;"); // yellow
            } else {
                bar.setStyle("-fx-accent: #81C784;"); // green
            }

            setGraphic(bar);
        }
    }
});


        // Status
        TableColumn<BudgetRow, String> statusCol =
                new TableColumn<>("Status");
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
        progressCol,
        statusCol
);


        refresh();

        // =====================================
        // EDIT BUTTON
        // =====================================

        Button editBtn = new Button("Edit Selected");

        editBtn.setOnAction(e -> {

            BudgetRow selected =
                    table.getSelectionModel().getSelectedItem();

            if (selected == null) return;

            if (selected.isEdited()) {
                new Alert(Alert.AlertType.INFORMATION,
                        "Budget can only be edited once per month"
                ).show();
                return;
            }

            TextInputDialog dialog =
                    new TextInputDialog(
                            String.valueOf(
                                    selected.getCurrentBudget()
                            )
                    );

            dialog.setTitle("Edit Budget");
            dialog.setHeaderText(
                    "Edit budget for "
                            + selected.getCategory()
            );
            dialog.setContentText("New limit:");

            dialog.showAndWait().ifPresent(value -> {
                try {

                    double newLimit =
                            Double.parseDouble(value);

                    budgetService.updateBudget(
                            selected.getCategory(),
                            newLimit
                    );

                    refresh();

                } catch (Exception ex) {
                    new Alert(Alert.AlertType.ERROR,
                            ex.getMessage()).show();
                }
            });
        });

        Button backBtn = new Button("Back");
        backBtn.setOnAction(e -> onBack.run());

        VBox top = new VBox(20,
                title,
                setSection
        );

        VBox bottom = new VBox(10,
                editBtn,
                backBtn
        );

        setTop(top);
        setCenter(table);
        setBottom(bottom);
    }

    private void refresh() {
        table.setItems(
                FXCollections.observableArrayList(
                        budgetService
                                .getCurrentMonthBudgetRows(expenses)
                )
        );
    }
}
