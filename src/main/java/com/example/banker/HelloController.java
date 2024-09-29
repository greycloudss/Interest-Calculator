package com.example.banker;


import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


public class HelloController {
    String[] text = new String[2];
    Loan loan;

    @FXML
    public ChoiceBox<String> choiceBox;

    @FXML
    private Button closeButton;

    @FXML
    private Button calcButton;

    @FXML
    public TextField zeroTextBox;

    @FXML
    public TextField oneTextBox;

    @FXML
    public void initialize() {
        initChoiceBox();
    }

    public void initChoiceBox() {
        choiceBox.getItems().addAll("Linear", "Exponential", "Annuential");
        choiceBox.setValue("Linear");
    }

    @FXML
    public void handleCloseButton() {
            Stage stage = (Stage) closeButton.getScene().getWindow();
            stage.close();
    }

    public void handleCalcButton() {
        this.text[0] = zeroTextBox.getText(); // loan amount
        this.text[1] = oneTextBox.getText(); // loan time
        final double interest = 3.5;

        if (text[0] == null || text[1] == null || text[0].isEmpty() || text[1].isEmpty())
            return;

        try {
            switch (choiceBox.getValue()) {
                case "Exponential":
                    loan = new Exponential(interest, Double.parseDouble(text[0]), Integer.parseInt(text[1]));
                    break;

                case "Annuential":
                    loan = new Annuential(interest, Double.parseDouble(text[0]), Integer.parseInt(text[1]));
                    break;

                default:
                    loan = new Linear(interest, Double.parseDouble(text[0]), Integer.parseInt(text[1]));
                    break;
            }
            double totalPayment = loan.getTotalPayment();
            System.out.println("Total Payment: " + totalPayment);
        } catch (NumberFormatException e) {
            System.err.println("Error parsing input: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class Loan {
    protected double interest;
    protected double borrowed;
    protected int monthsToPay;

    Loan() {
        borrowed = 0;
        monthsToPay = 0;
        interest = 0;
    }

    Loan(double interest, double borrowed, int monthsToPay) {
        this.interest = interest;
        this.borrowed = borrowed;
        this.monthsToPay = monthsToPay;
    }

    double getTotalPayment() {
        return 0;
    }

    double getMonthlyPayment() {
        return 0;
    }

    int getMonthsToPay() { return monthsToPay; }
    double getInterest() { return interest; }
    double getBorrowed() { return borrowed; }
}

class Linear extends Loan {

    Linear(double interest, double borrowed, int monthsToPay) {
        super(interest, borrowed, monthsToPay);
    }

    @Override
    double getTotalPayment() {
        return borrowed + (borrowed * (interest / 100) * monthsToPay);
    }

    @Override
    double getMonthlyPayment() {
        return getTotalPayment() / monthsToPay;
    }
}

class Exponential extends Loan {

    Exponential(double interest, double borrowed, int monthsToPay) {
        super(interest, borrowed, monthsToPay);
    }

    @Override
    double getTotalPayment() {
        return borrowed * Math.pow(1 + (interest / 100), monthsToPay);
    }
}

class Annuential extends Loan {

    Annuential(double interest, double borrowed, int monthsToPay) {
        super(interest, borrowed, monthsToPay);
    }

    @Override
    double getTotalPayment() {
        double monthlyInterestRate = interest / 100 / 12;
        return (borrowed * monthlyInterestRate * Math.pow(1 + monthlyInterestRate, monthsToPay)) /
                (Math.pow(1 + monthlyInterestRate, monthsToPay) - 1) * monthsToPay;
    }

}