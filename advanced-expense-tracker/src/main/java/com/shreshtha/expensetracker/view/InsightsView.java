package com.shreshtha.expensetracker.view;

import com.shreshtha.expensetracker.database.ExpenseRepository;
import com.shreshtha.expensetracker.model.Expense;
import javafx.geometry.Insets;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class InsightsView extends VBox {

    public InsightsView(ExpenseRepository repo, Runnable onBack) {

        setPadding(new Insets(30));
        setSpacing(25);

        Label title = new Label("Insights");
        title.getStyleClass().add("page-title");

        Label subtitle = new Label("Understand your spending behavior");
        subtitle.getStyleClass().add("page-subtitle");

        String topCategory = repo.getHighestCategory();
        String topMood = repo.getHighestMood();

        Label insight1 = new Label("• You spend the most on: " + topCategory);
        Label insight2 = new Label("• You spend the most when feeling: " + topMood);

        insight1.getStyleClass().add("insight-text");
        insight2.getStyleClass().add("insight-text");

        CategoryAxis dateAxis = new CategoryAxis();
        NumberAxis amountAxis = new NumberAxis();
        amountAxis.setLabel("Amount Spent");

        LineChart<String, Number> trendChart =
                new LineChart<>(dateAxis, amountAxis);

        trendChart.setTitle("Spending Trend Over Time");
        trendChart.setLegendVisible(false);
        trendChart.setPrefHeight(280);

        Map<String, Double> dailyTotals = new TreeMap<>();

        for (Expense e : repo.getAllExpenses()) {
            dailyTotals.put(
                e.getDate(),
                dailyTotals.getOrDefault(e.getDate(), 0.0) + e.getAmount()
            );
        }

        XYChart.Series<String, Number> trendSeries = new XYChart.Series<>();
        dailyTotals.forEach((d, v) ->
            trendSeries.getData().add(new XYChart.Data<>(d, v))
        );

        trendChart.getData().add(trendSeries);

        // piechart
        PieChart moodChart = new PieChart();
        moodChart.setTitle("Spending by Mood");
        moodChart.setLabelsVisible(true);

        Map<String, Double> moodTotals = new HashMap<>();
        for (Expense e : repo.getAllExpenses()) {
            moodTotals.put(
                e.getMood(),
                moodTotals.getOrDefault(e.getMood(), 0.0) + e.getAmount()
            );
        }

        moodTotals.forEach((m, v) ->
            moodChart.getData().add(new PieChart.Data(m, v))
        );

        HBox charts = new HBox(30, trendChart, moodChart);

        getChildren().addAll(
                title,
                subtitle,
                insight1,
                insight2,
                charts
        );
    }
}
