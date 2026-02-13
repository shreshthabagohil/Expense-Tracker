package com.shreshtha.expensetracker.database;

import com.shreshtha.expensetracker.model.MonthKey;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class BudgetStorage {

    public static void save(String username, Map<MonthKey, Map<String, Double>> data) {

        String file = "budgets_" + username + ".csv";

        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            for (var monthEntry : data.entrySet()) {
                MonthKey key = monthEntry.getKey();
                for (var cat : monthEntry.getValue().entrySet()) {
                    pw.println(
                        key.toString() + "," +
                        cat.getKey() + "," +
                        cat.getValue()
                    );
                }
            }
        } catch (IOException e) {
            System.out.println("Error saving budgets");
        }
    }

    public static Map<MonthKey, Map<String, Double>> load(String username) {

        Map<MonthKey, Map<String, Double>> result = new HashMap<>();
        String file = "budgets_" + username + ".csv";

        File f = new File(file);
        if (!f.exists()) return result;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {

                String[] d = line.split(",");
                String[] ym = d[0].split("-");

                MonthKey key = MonthKey.from(
                    Integer.parseInt(ym[0]),
                    Integer.parseInt(ym[1])
                );

                result.putIfAbsent(key, new HashMap<>());
                result.get(key).put(d[1], Double.parseDouble(d[2]));
            }
        } catch (IOException e) {
            System.out.println("Error loading budgets");
        }

        return result;
    }
}
