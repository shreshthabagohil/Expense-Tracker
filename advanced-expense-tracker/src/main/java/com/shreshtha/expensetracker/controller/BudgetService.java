package com.shreshtha.expensetracker.controller;

import com.shreshtha.expensetracker.database.BudgetStorage;
import com.shreshtha.expensetracker.database.BudgetStorage.BudgetDataDTO;
import com.shreshtha.expensetracker.model.*;

import java.util.*;

public class BudgetService {

    private Map<MonthKey, Map<String, BudgetDataDTO>> monthlyBudgets;
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

        monthlyBudgets.get(current).put(
                category,
                new BudgetDataDTO(limit, limit, false)
        );

        BudgetStorage.save(username, monthlyBudgets);
    }

    public void updateBudget(String category, double newLimit) {

        MonthKey current = MonthKey.current();

        if (!monthlyBudgets.containsKey(current) ||
                !monthlyBudgets.get(current).containsKey(category)) {
            throw new IllegalStateException("Budget does not exist");
        }

        BudgetDataDTO data = monthlyBudgets.get(current).get(category);

        if (data.edited) {
            throw new IllegalStateException("Budget can only be edited once");
        }

        data.current = newLimit;
        data.edited = true;

        BudgetStorage.save(username, monthlyBudgets);
    }

    public List<BudgetRow> getCurrentMonthBudgetRows(List<Expense> expenses) {

        MonthKey current = MonthKey.current();

        Map<String, BudgetDataDTO> limits =
                monthlyBudgets.getOrDefault(current, new HashMap<>());

        Map<String, Double> spent = new HashMap<>();

        for (Expense e : expenses) {
            spent.merge(e.getCategory(), e.getAmount(), Double::sum);
        }

        List<BudgetRow> rows = new ArrayList<>();

        for (String cat : limits.keySet()) {

            BudgetDataDTO data = limits.get(cat);

            rows.add(new BudgetRow(
                    cat,
                    data.original,
                    data.current,
                    spent.getOrDefault(cat, 0.0),
                    data.edited
            ));
        }

        return rows;
    }
}
