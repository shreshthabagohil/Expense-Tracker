package com.shreshtha.expensetracker;

import com.shreshtha.expensetracker.controller.AppController;

public class App {
    public static void main(String[] args) {

        AppController controller = new AppController();
        controller.start();
    }
}
