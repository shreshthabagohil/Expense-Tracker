package com.shreshtha.expensetracker.model;

import java.util.List;

public class CategoryUtil {

    public static List<String> getCategories() {
        return List.of(
                "Bills",
                "Food",
                "Transport",
                "Shopping",
                "Entertainment",
                "Other"
        );
    }
}
