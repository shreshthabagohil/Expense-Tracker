package com.shreshtha.expensetracker;

import com.shreshtha.expensetracker.controller.SummaryService;
import com.shreshtha.expensetracker.model.Expense;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SummaryServiceTest 
{

    @Test
    void testTotalAmountCalculation() 
    {

        ArrayList<Expense> expenses = new ArrayList<>();
        expenses.add(new Expense(100, "Food", "2025-01-01", "Happy"));
        expenses.add(new Expense(200, "Travel", "2025-01-02", "Neutral"));

        SummaryService service = new SummaryService();
        var summary = service.generate(expenses);

        assertEquals(300, summary.getTotalAmount());
    }
}
