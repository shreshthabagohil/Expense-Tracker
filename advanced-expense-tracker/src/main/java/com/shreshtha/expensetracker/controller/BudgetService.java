package com.shreshtha.expensetracker.controller;

import com.shreshtha.expensetracker.database.BudgetStorage;
import com.shreshtha.expensetracker.model.*;

import java.util.*;

public class BudgetService {

    private Map<MonthKey, Map<String, Double>> monthlyBudgets;
    private String username;

    public void setUser(String username) {
        this.username = username;
        this.monthlyBudgets = BudgetStorage.load(username);
    }

    public void setBudgetOnce(String category, double limit) {
        MonthKey current = MonthKey.current();
        monthlyBudgets.putIfAbsent(current, new HashMap<>());

        if (monthlyBudgets.get(current).containsKey(category)) {
            throw new IllegalStateException("Budget already set for this month");
        }

        monthlyBudgets.get(current).put(category, limit);
        BudgetStorage.save(username, monthlyBudgets);
    }

    public void checkBudgets(List<Expense> expenses) {
        MonthKey current = MonthKey.current();
        if (!monthlyBudgets.containsKey(current)) return;

        Map<String, Double> limits = monthlyBudgets.get(current);
        Map<String, Double> spent = new HashMap<>();

        for (Expense e : expenses) {
            spent.merge(e.getCategory(), e.getAmount(), Double::sum);
        }

        for (String cat : limits.keySet()) {
            double s = spent.getOrDefault(cat, 0.0);
            double l = limits.get(cat);

            if (s >= l) {
                System.out.println("Budget exceeded for " + cat);
            }
        }
    }

    public List<BudgetRow> getCurrentMonthBudgetRows(List<Expense> expenses) {

        MonthKey current = MonthKey.current();
        Map<String, Double> limits =
            monthlyBudgets.getOrDefault(current, new HashMap<>());

        Map<String, Double> spent = new HashMap<>();
        for (Expense e : expenses) {
            spent.merge(e.getCategory(), e.getAmount(), Double::sum);
        }

        List<BudgetRow> rows = new ArrayList<>();
        for (String cat : limits.keySet()) {
            rows.add(new BudgetRow(
                cat,
                limits.get(cat),
                spent.getOrDefault(cat, 0.0)
            ));
        }

        return rows;
    }

     public boolean hasBudgetForCategory(String category) 
     {
         MonthKey current = MonthKey.current();
         return monthlyBudgets.containsKey(current)
            && monthlyBudgets.get(current).containsKey(category);
     }

     public void updateBudget(String category, double newLimit) {
        MonthKey current = MonthKey.current();

       if (!hasBudgetForCategory(category)) {
            throw new IllegalStateException(
                "Cannot edit budget that does not exist"
            );
        }
        monthlyBudgets.get(current).put(category, newLimit);
        BudgetStorage.save(username, monthlyBudgets);
    }
  

}
