
package com.shreshtha.expensetracker.controller;

import com.shreshtha.expensetracker.database.ExpenseRepository;
import com.shreshtha.expensetracker.model.Expense;
import com.shreshtha.expensetracker.view.ConsoleView;
import com.shreshtha.expensetracker.controller.SummaryService;

import java.util.Scanner;

public class AppController {

    private ExpenseRepository repo = new ExpenseRepository();
    private ConsoleView view = new ConsoleView();
    private SummaryService summaryService = new SummaryService();
    private String username;
    private BudgetService budgetService=new BudgetService();
    private Scanner sc = new Scanner(System.in);

    public void start() {
        System.out.println("Enter username: ");
        username=sc.nextLine();
        repo.setUser(username);
        budgetService.setUser(username);
        
        boolean running = true;

        while (running) {
            view.showMenu();
            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1 -> addExpense();
                case 2 -> viewExpenses();
                case 3 -> showMonthlySummary();
                case 4 -> running = false;
                case 5 -> setBudget();
                default -> view.showMessage("Invalid choice!");
            }
        }
    }

    private void addExpense() {
        System.out.print("Amount: ");
        double amount = sc.nextDouble();
        sc.nextLine();

        System.out.print("Category: ");
        String category = sc.nextLine();

        System.out.print("Date (YYYY-MM-DD): ");
        String date = sc.nextLine();

        System.out.print("Mood: ");
        String mood = sc.nextLine();

        System.out.println("Description: ");
        String description=sc.nextLine();

        Expense e = new Expense(amount, category, date, mood,description);
        repo.addExpense(e);

        budgetService.checkBudgets(repo.getAllExpenses());

        view.showMessage("Expense added successfully!");
    }

    private void viewExpenses() {
        for (Expense e : repo.getAllExpenses()) {
            System.out.println(e);
        }
    }

    private void showMonthlySummary() 
    {
      var summary = summaryService.generate(repo.getAllExpenses());

      System.out.println("Total Spent: " + summary.getTotalAmount());
      System.out.println("Highest Category: " + summary.getHighestCategory());
      System.out.println("Category Breakdown:");

      summary.getCategoryTotals().forEach((k, v) ->
        System.out.println(k + " -> " + v)
      );

      System.out.println();
      summaryService.printInsights(repo.getAllExpenses());
   }

   private void setBudget()
    {
        System.out.println("Category: ");
        String category=sc.nextLine();

        System.out.println("Monthly Limit: ");
        double limit=sc.nextDouble();
        sc.nextLine();

        try {
           budgetService.setBudgetOnce(category, limit);
            System.out.println("Budget set successfully!");
        }
         catch (IllegalStateException e) {
            System.out.println(e.getMessage());
           }
    }
}

