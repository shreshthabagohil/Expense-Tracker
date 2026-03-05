package com.shreshtha.expensetracker.view;

import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;

import com.shreshtha.expensetracker.controller.BudgetService;
import com.shreshtha.expensetracker.database.ExpenseRepository;
import com.shreshtha.expensetracker.model.Expense;

import javafx.geometry.Insets;
import javafx.geometry.Pos;

public class AddExpenseView extends VBox
{

  private ExpenseRepository repo;
  private BudgetService budgetService;

    public AddExpenseView(Runnable onBack,ExpenseRepository repo,BudgetService budgetService) 
    {

      this.repo=repo;
      this.budgetService=budgetService;
      setPadding(new Insets(40));
      setAlignment(Pos.TOP_CENTER);

      Label title = new Label("Add Expense");
      title.setStyle("""
        -fx-font-size: 26px;
        -fx-font-weight: bold;
      """);

      TextField amountField = new TextField();
      amountField.setPromptText("Amount");
      amountField.setMaxWidth(300);

      ComboBox<String> categoryBox = new ComboBox<>();
      categoryBox.getItems().addAll(
              com.shreshtha.expensetracker.model.CategoryUtil.getCategories()
      );
      categoryBox.setPromptText("Category");
      categoryBox.setMaxWidth(300);

      ComboBox<String> moodBox=new ComboBox<>();
      moodBox.getItems().addAll("Happy 🙂","Neutral 😐","Sad 🙁","Stressed 😣");
      moodBox.setPromptText("Mood");
      moodBox.setMaxWidth(300);

      TextField descriptionField = new TextField();
      descriptionField.setPromptText("Description");
      descriptionField.setMaxWidth(300);

      DatePicker datePicker = new DatePicker();
      datePicker.setPromptText("Date");
      datePicker.setMaxWidth(300);

      Button saveBtn = iconButton("Save", "/icons/save.png");
      Button backBtn = iconButton("Back", "/icons/back.png");

      backBtn.setOnAction(e -> onBack.run());

      HBox actions = new HBox(15, saveBtn, backBtn);
      actions.setAlignment(Pos.CENTER);

       VBox card = new VBox(15,
            title,
            amountField,
            categoryBox,
            moodBox,
            descriptionField,
            datePicker,
            actions
       );

       saveBtn.setOnAction(e->
        {
            try{
            double amount=Double.parseDouble(amountField.getText());
            String category=categoryBox.getValue();
            String description= descriptionField.getText();
            String mood=moodBox.getValue();
            String date=datePicker.getValue().toString();

            if(category==null|| mood==null || datePicker.getValue()==null)
            {
                showAlert("Please fill all fields");
                return;
            }

            Expense expense=new Expense(amount, category, date, mood,description);

            repo.addExpense(expense);
            List<String> warnings =
        budgetService.checkBudgetWarnings(
                repo.getAllExpenses()
        );

if (!warnings.isEmpty()) {

    Alert alert =
            new Alert(Alert.AlertType.WARNING);

    alert.setTitle("Budget Warning");
    alert.setHeaderText("Attention!");

    alert.setContentText(
            String.join("\n", warnings)
    );

    alert.getDialogPane().getStylesheets().add(
            getClass()
                    .getResource("/theme.css")
                    .toExternalForm()
    );

    alert.showAndWait();
}

            showAlert("Expense saved successfully!");

            amountField.clear();
            descriptionField.clear();
            categoryBox.setValue(null);
            datePicker.setValue(null);
            moodBox.setValue(null);
           }
           catch(Exception ex)
           {
              showAlert("Invalid input!");
           }
        });


        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(30));
        card.setMaxWidth(400);
        card.setStyle("""
        -fx-background-color: #ffffff;
        -fx-background-radius: 12;
        -fx-border-radius: 12;
        -fx-border-color: #dddddd;
         """);


       getChildren().add(card);
    } 


    private Button iconButton(String text,String iconPath)
    {

        ImageView icon = new ImageView(
        new Image(getClass().getResourceAsStream(iconPath))
        );

        icon.setFitWidth(16);
        icon.setFitHeight(16);

        Button btn =new Button(text,icon);
        btn.setContentDisplay(ContentDisplay.LEFT);
        btn.setStyle(
            "-fx-background-radius: 8;" +
            "-fx-padding: 8 14;"
        );

        return btn;
    }

    private void showAlert(String msg) {
    Alert alert = new Alert(Alert.AlertType.NONE);
    alert.setTitle("Message");
    alert.setHeaderText(null);
    alert.setContentText(msg);

    ButtonType ok = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
    alert.getButtonTypes().add(ok);

    alert.setGraphic(null);

    alert.getDialogPane().getStylesheets().add(
        getClass().getResource("/theme.css").toExternalForm()
    );

    alert.showAndWait();
}

}
