package com.shreshtha.expensetracker.controller;

import com.shreshtha.expensetracker.model.Expense;
import com.shreshtha.expensetracker.model.MonthlySummary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SummaryService {

    public MonthlySummary generate(List<Expense> expenses) 
    {
        double total = 0;
        Map<String, Double> categoryTotals = new HashMap<>();

        for (Expense e : expenses) {
            total += e.getAmount();
            categoryTotals.put(
                e.getCategory(),
                categoryTotals.getOrDefault(e.getCategory(), 0.0) + e.getAmount()
            );
        }

        String highestCategory = categoryTotals.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("None");

        return new MonthlySummary(expenses, total, categoryTotals, highestCategory);
    }

    public Map<String, Double> spendingByMood(List<Expense> expenses) 
    {
        Map<String,Double> moodTotals =new HashMap<>();

        for(Expense e:expenses)
        {
            moodTotals.put(e.getMood(),moodTotals.getOrDefault(e.getMood(), 0.0)+e.getAmount());
        }

        return moodTotals;
    }

    public Map<String,Double> averageSpendingPerCategory(List<Expense> expenses)
    {

        Map<String,Double> total=new HashMap<>();
        Map<String,Integer> count=new HashMap<>();

        for(Expense e: expenses)
        {
            total.put(e.getCategory(), total.getOrDefault(e.getCategory(), 0.0)+e.getAmount());

            count.put(e.getCategory(),count.getOrDefault(e.getCategory(), 0)+1);
        }

        Map<String,Double> average=new HashMap<>();
        for(String category:total.keySet())
        {
            average.put(category,total.get(category)/count.get(category));
        }

        return average;
    }


    public void printInsights(List<Expense> expenses)
    {

        if(expenses.isEmpty())
        {
            System.out.println("No insights available yet");
            return;
        }

        Map<String,Double>moodMap= spendingByMood(expenses);

        String highestMood= moodMap.entrySet().stream()
        .max(Map.Entry.comparingByValue()).get().getKey();

        System.out.println("Insight: You spend the most when your mood is \"" + highestMood + "\"");

        Map<String ,Double> avgCategory= averageSpendingPerCategory(expenses);

        System.out.println("Average spending per category:");
        avgCategory.forEach((cat, avg) ->
        System.out.println("- " + cat + ": " + avg)
    );
    }
}

