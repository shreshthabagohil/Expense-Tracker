package com.shreshtha.expensetracker.database;

import com.shreshtha.expensetracker.model.Expense;

import java.io.*;
import java.util.ArrayList;

public class FileStorage
{

     public static void save(String username,ArrayList<Expense> expenses)
     {

        String filename="expenses_"+username+".csv";
        try(PrintWriter pw=new PrintWriter(new FileWriter(filename)))
        {
            for(Expense e:expenses)
            {
                pw.println(
                    e.getAmount()+","+
                    e.getCategory()+","+
                    e.getDate()+","+
                    e.getMood()+","+
                    e.getDescription()
                );
            }
        }
        catch (IOException e)
        {
            System.out.println("Error saving file");
        }
     }

     public static ArrayList<Expense> load(String username)
     {

        ArrayList<Expense> expenses=new ArrayList<>();
        String filename="expenses_"+username+".csv";

        File file=new File(filename);
        if(!file.exists()) return expenses;
        try (BufferedReader br=new BufferedReader(new FileReader(filename)))
        {
            String line;
            while((line=br.readLine())!=null)
            {
                String[] data=line.split(",");

                if (data.length < 5) continue;

                Expense e=new Expense(
                    Double.parseDouble(data[0]),data[1],data[2],data[3],data[4]
                );

                expenses.add(e);
            }
        }
        catch (IOException e)
        {
            System.out.println("Error loading file");
        }
        return expenses;
     }
}