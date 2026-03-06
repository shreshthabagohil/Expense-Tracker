package com.shreshtha.expensetracker.view;

import com.shreshtha.expensetracker.database.ExpenseRepository;
import com.shreshtha.expensetracker.model.Expense;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashBoardView extends VBox {

    private BarChart<String, Number> chart;
    private ExpenseRepository repo;

    public DashBoardView(String username, ExpenseRepository repo) {

        this.repo = repo;

        setPadding(new Insets(30));
        setSpacing(20);
        setAlignment(Pos.TOP_CENTER);

        Label title = new Label("Welcome, " + username + " 👋");
        title.getStyleClass().add("page-title");

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Amount Spent");

        chart = new BarChart<>(xAxis, yAxis);
        chart.setLegendVisible(false);
        chart.setAnimated(false);
        chart.setPrefHeight(350);

        getChildren().addAll(title, chart);

        refresh();
    }

    public void refresh() {
        chart.getData().clear();

        Map<String, Double> totals = new HashMap<>();

        for (Expense e : repo.getAllExpenses()) {
            totals.merge(
                    e.getCategory(),
                    e.getAmount(),
                    Double::sum
            );
        }

        XYChart.Series<String, Number> series =
                new XYChart.Series<>();

        totals.forEach((cat, amt) ->
                series.getData().add(
                        new XYChart.Data<>(cat, amt)
                )
        );

        chart.getData().add(series);
    }
}
