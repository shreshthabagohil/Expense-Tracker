package com.shreshtha.expensetracker.view;

public class ConsoleView {

    public void showMenu() {
        System.out.println("\n==== EXPENSE TRACKER ====");
        System.out.println("1. Add Expense");
        System.out.println("2. View All Expenses");
        System.out.println("3. Monthly Summary");
        System.out.println("4. Exit");
        System.out.println("5. Set Budget");

        System.out.print("Choose an option: ");
    }

    public void showMessage(String msg) {
        System.out.println(msg);
    }

    public void showExpense(String expense) {
        System.out.println(expense);
    }

}
//this will help separate ui and logic