package com.shreshtha.expensetracker.database;

import com.shreshtha.expensetracker.model.MonthKey;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class BudgetStorage {

    // Save structure:
    // year-month,category,original,current,edited
    public static void save(String username,
            Map<MonthKey, Map<String, BudgetDataDTO>> data) {

        String file = "budgets_" + username + ".csv";

        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {

            for (var monthEntry : data.entrySet()) {

                MonthKey key = monthEntry.getKey();

                for (var cat : monthEntry.getValue().entrySet()) {

                    BudgetDataDTO dto = cat.getValue();

                    pw.println(
                            key.toString() + "," +
                            cat.getKey() + "," +
                            dto.original + "," +
                            dto.current + "," +
                            dto.edited
                    );
                }
            }

        } catch (IOException e) {
            System.out.println("Error saving budgets");
        }
    }

    public static Map<MonthKey, Map<String, BudgetDataDTO>> load(String username) {

        Map<MonthKey, Map<String, BudgetDataDTO>> result = new HashMap<>();

        String file = "budgets_" + username + ".csv";
        File f = new File(file);

        if (!f.exists()) return result;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {

            String line;

            while ((line = br.readLine()) != null) {

                String[] d = line.split(",");

                // Backward compatibility (old files)
                if (d.length < 5) continue;

                String[] ym = d[0].split("-");

                MonthKey key = MonthKey.from(
                        Integer.parseInt(ym[0]),
                        Integer.parseInt(ym[1])
                );

                result.putIfAbsent(key, new HashMap<>());

                BudgetDataDTO dto = new BudgetDataDTO(
                        Double.parseDouble(d[2]),  // original
                        Double.parseDouble(d[3]),  // current
                        Boolean.parseBoolean(d[4]) // edited
                );

                result.get(key).put(d[1], dto);
            }

        } catch (IOException e) {
            System.out.println("Error loading budgets");
        }

        return result;
    }

    // DTO for storage only (NOT business logic)
    public static class BudgetDataDTO {
        public double original;
        public double current;
        public boolean edited;

        public BudgetDataDTO(double original, double current, boolean edited) {
            this.original = original;
            this.current = current;
            this.edited = edited;
        }
    }
}
