package com.shreshtha.expensetracker.view;

import com.shreshtha.expensetracker.database.ExpenseRepository;
import com.shreshtha.expensetracker.model.Expense;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.Map;

public class DashBoardView extends VBox {

    public DashBoardView(String username, ExpenseRepository repo) {

        setPadding(new Insets(30));
        setSpacing(20);
        setAlignment(Pos.TOP_CENTER);

        Label title = new Label("Welcome, " + username + " 👋");
        title.getStyleClass().add("page-title");

        Label subtitle = new Label("Your category-wise expense overview");
        subtitle.getStyleClass().add("page-subtitle");

        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Category");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Amount Spent");

        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setLegendVisible(false);
        chart.setAnimated(false);
        chart.setPrefHeight(350);

        Map<String, Double> totals = new HashMap<>();
        for (Expense e : repo.getAllExpenses()) {
            totals.put(
                e.getCategory(),
                totals.getOrDefault(e.getCategory(), 0.0) + e.getAmount()
            );
        }

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        totals.forEach((cat, amt) ->
            series.getData().add(new XYChart.Data<>(cat, amt))
        );

        chart.getData().add(series);

        chart.lookupAll(".default-color0.chart-bar")
             .forEach(n -> n.setStyle("-fx-bar-fill: #6C7CF5;"));


        getChildren().addAll(title, subtitle, chart);
    }
}
