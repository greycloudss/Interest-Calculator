package com.example.banker;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class HelloController {
    private final String[] inputTexts = new String[2];
    private Loan loan;
    private XYChart.Series<Number, Number> series;

    @FXML
    private ChoiceBox<String> choiceBox;

    @FXML
    private Button closeButton;

    @FXML
    private Button calcButton;

    @FXML
    private TextField zeroTextBox;

    @FXML
    private TextField oneTextBox;

    @FXML
    private LineChart<Number, Number> lineChart;

    @FXML
    private NumberAxis xAxis;

    @FXML
    private NumberAxis yAxis;

    @FXML
    private Text monthlyPaymentText;

    @FXML
    private Text totalPaymentText;

    @FXML
    private Text totalInterestText;

    @FXML
    private VBox dataBox;

    @FXML
    public void initialize() {
        initChoiceBox();
        configureChart();
    }

    private void initChoiceBox() {
        choiceBox.getItems().addAll("Linear", "Exponential", "Annuential");
        choiceBox.setValue("Linear");
    }

    private void configureChart() {
        lineChart.setStyle("-fx-background-color: white;");
        xAxis.setTickLabelFill(Color.BLACK);
        yAxis.setTickLabelFill(Color.BLACK);
    }

    @FXML
    private void handleCloseButton() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleCalcButton() {
        inputTexts[0] = zeroTextBox.getText();
        inputTexts[1] = oneTextBox.getText();
        final double interest = 3.5;

        if (inputTexts[0].isEmpty() || inputTexts[1].isEmpty()) {
            return;
        }

        try {
            switch (choiceBox.getValue()) {
                case "Exponential":
                    loan = new Exponential(interest, Double.parseDouble(inputTexts[0]), Integer.parseInt(inputTexts[1]));
                    break;
                case "Annuential":
                    loan = new Annuential(interest, Double.parseDouble(inputTexts[0]), Integer.parseInt(inputTexts[1]));
                    break;
                default:
                    loan = new Linear(interest, Double.parseDouble(inputTexts[0]), Integer.parseInt(inputTexts[1]));
                    break;
            }

            double totalPayment = loan.getTotalPayment();
            double monthlyPayment = loan.getMonthlyPayment();
            double totalInterest = totalPayment - loan.getBorrowed();

            populateData();
            displayLoanDetails(monthlyPayment, totalPayment, totalInterest);

        } catch (NumberFormatException e) {
            System.err.println("Error parsing input: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void populateData() {
        lineChart.getData().clear();
        series = new XYChart.Series<>();
        series.setName(choiceBox.getValue() + " Loan Interest");

        for (int i = 1; i <= loan.getMonthsToPay(); i++) {
            double interestAccumulated = loan.getAccumulatedInterest(i);
            series.getData().add(new XYChart.Data<>(i, interestAccumulated));
        }

        lineChart.getData().add(series);
    }

    private void displayLoanDetails(double monthlyPayment, double totalPayment, double totalInterest) {
        monthlyPaymentText.setText("Monthly Payment: $" + String.format("%.2f", monthlyPayment));
        totalPaymentText.setText("Total Payment: $" + String.format("%.2f", totalPayment));
        totalInterestText.setText("Total Interest: $" + String.format("%.2f", totalInterest));
    }
}

class Loan {
    protected double interest;
    protected double borrowed;
    protected int monthsToPay;

    Loan() {
        this.borrowed = 0;
        this.monthsToPay = 0;
        this.interest = 0;
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

    int getMonthsToPay() {
        return monthsToPay;
    }

    double getInterest() {
        return interest;
    }

    double getBorrowed() {
        return borrowed;
    }

    double getAccumulatedInterest(int month) {
        return 0;
    }
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

    @Override
    double getAccumulatedInterest(int month) {
        return borrowed * (interest / 100) * month;
    }
}

class Exponential extends Loan {
    Exponential(double interest, double borrowed, int monthsToPay) {
        super(interest, borrowed, monthsToPay);
    }

    @Override
    double getTotalPayment() {
        return getMonthlyPayment() * monthsToPay; // Total payment is monthly payment times number of months
    }

    @Override
    double getMonthlyPayment() {
        double monthlyRate = (interest / 100) / 12; // Convert annual interest rate to monthly
        if (monthlyRate == 0) {
            return borrowed / monthsToPay; // If interest rate is 0, simple division
        }
        return (borrowed * monthlyRate * Math.pow(1 + monthlyRate, monthsToPay)) /
                (Math.pow(1 + monthlyRate, monthsToPay) - 1);
    }

    @Override
    double getAccumulatedInterest(int month) {
        return borrowed * (Math.pow(1 + (interest / 100) / 12, month) - 1);
    }
}

class Annuential extends Loan {
    Annuential(double interest, double borrowed, int monthsToPay) {
        super(interest, borrowed, monthsToPay);
    }

    @Override
    double getTotalPayment() {
        double monthlyInterestRate = interest / 100 / 12;
        return getMonthlyPayment() * monthsToPay; // Total payment is monthly payment times number of months
    }

    @Override
    double getMonthlyPayment() {
        double monthlyInterestRate = interest / 100 / 12;
        if (monthlyInterestRate == 0) {
            return borrowed / monthsToPay; // If interest rate is 0, simple division
        }
        return (borrowed * monthlyInterestRate * Math.pow(1 + monthlyInterestRate, monthsToPay)) /
                (Math.pow(1 + monthlyInterestRate, monthsToPay) - 1);
    }

    @Override
    double getAccumulatedInterest(int month) {
        double monthlyInterestRate = interest / 100 / 12;
        return (borrowed * (Math.pow(1 + monthlyInterestRate, month) - 1));
    }
}