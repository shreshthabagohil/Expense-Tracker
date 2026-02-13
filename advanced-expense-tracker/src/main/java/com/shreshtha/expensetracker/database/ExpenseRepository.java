package com.shreshtha.expensetracker.database;

import com.shreshtha.expensetracker.model.Expense;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ExpenseRepository {

    private ArrayList<Expense> expenses = new ArrayList<>();
    private String username;

    public void setUser(String username) {
        this.username = username;
        this.expenses = FileStorage.load(username);
    }

    public void addExpense(Expense expense) {
        if (expenses == null) {
            expenses = new ArrayList<>();
        }
        expenses.add(expense);
        FileStorage.save(username, expenses);
    }

    public ArrayList<Expense> getAllExpenses() {
        return expenses;
    }

    public double getTotalExpenses() {
        double sum=0;
        for(Expense e:expenses)
        {
            sum+=e.getAmount();
        }
        return sum;
    }

    public String getHighestCategory() 
    {
        Map<String, Double> map = new HashMap<>();

        for (Expense e : expenses) {
          map.put(
              e.getCategory(),
              map.getOrDefault(e.getCategory(), 0.0) + e.getAmount()
          );
       }

       String maxCat = "None";
       double max = 0;

       for (String cat : map.keySet()) {
         if (map.get(cat) > max) {
             max = map.get(cat);
             maxCat = cat;
         }
       }
        return maxCat;
     }

     public String getHighestMood() {
     Map<String, Double> moodMap = new HashMap<>();

     for (Expense e : expenses) {
         moodMap.put(
             e.getMood(),
             moodMap.getOrDefault(e.getMood(), 0.0) + e.getAmount()
         );
     }

     String topMood = "None";
     double max = 0;

      for (String mood : moodMap.keySet()) {
          if (moodMap.get(mood) > max) {
             max = moodMap.get(mood);
             topMood = mood;
          }
      }
      return topMood;
   }

   public void deleteExpense(Expense expense)
   {
      expenses.remove(expense);
      FileStorage.save(username, expenses);
   }

   public Map<String, Double> getCategoryTotals() {
    Map<String, Double> map = new HashMap<>();
    for (Expense e : expenses) {
        map.put(
            e.getCategory(),
            map.getOrDefault(e.getCategory(), 0.0) + e.getAmount()
        );
    }
    return map;
}

}



//for now act like database
//convert to sql later