package com.example.banker;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import loan.Annuential;
import loan.Exponential;
import loan.Linear;
import loan.Loan;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class HelloController {
    private Loan loan;
    private int calculationsDone = 0;

    @FXML
    private ChoiceBox<String> choiceBox;

    @FXML
    private Button closeButton;

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
    private TableView<PaymentRow> tableA;

    @FXML
    private TableColumn<PaymentRow, String> monthColumn;

    @FXML
    public void initialize() {
        initChoiceBox();
        configureChart();
        monthlyPaymentText.setVisible(false);
        totalPaymentText.setVisible(false);
        totalInterestText.setVisible(false);
        tableA.setVisible(false);
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
        tableA.getColumns().clear();
        String[] inputTexts = {zeroTextBox.getText(), oneTextBox.getText()};
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

            loan.calculateAndStoreMonthlyPayments();

            Double[] monthlyPayments = loan.getMonthlyPaymentsData();
            int totalMonths = loan.getMonthsToPay();
            int totalYears = (int) Math.ceil((double) totalMonths / 12.0);

            monthColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getMonth()));
            tableA.getColumns().add(monthColumn);

            for (int year = 1; year <= totalYears; year++) {
                TableColumn<PaymentRow, Double> yearColumn = new TableColumn<>("Year " + year);
                final int yearIndex = year;
                yearColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getYear(yearIndex)));
                tableA.getColumns().add(yearColumn);
            }

            ObservableList<PaymentRow> data = FXCollections.observableArrayList();

            for (int month = 0; month < 12; month++) {
                PaymentRow row = new PaymentRow("Month " + (month + 1), totalYears);
                for (int year = 1; year <= totalYears; year++) {
                    int paymentIndex = (year - 1) * 12 + month;
                    if (paymentIndex < totalMonths) {
                        row.setYear(year, monthlyPayments[paymentIndex]);
                    }
                }
                data.add(row);
            }

            tableA.setItems(data);
            monthlyPaymentText.setVisible(true);
            totalPaymentText.setVisible(true);
            totalInterestText.setVisible(true);
            tableA.setVisible(true);

            populateData();
            displayLoanDetails(monthlyPayments[0], loan.getTotalPayment(), loan.getAccumulatedInterest(totalMonths));

            calculationsDone++;

        } catch (NumberFormatException e) {
            System.err.println("Error parsing input: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleOutputBtn() throws IOException {
        if (calculationsDone <= 0)
            return;

        BufferedWriter writer = new BufferedWriter(new FileWriter("monthly_payments.txt"));

        int counter = 0;

        for (int i = 0; i < loan.getMonthsToPay(); ++i) {
            if (i % 12 == 0) {
                counter++;
                writer.write("\nyear " + counter + " payments\n");
            }
            writer.write(String.format("Month %d %.2f\n", (i % 12 + 1), loan.getMonthlyPaymentsData()[i]));
        }

        writer.close();
    }

    private void populateData() {
        lineChart.getData().clear();
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName(choiceBox.getValue() + " Loan Interest");

        for (int i = 1; i <= loan.getMonthsToPay(); i++) {
            double interestAccumulated = loan.getAccumulatedInterest(i);
            series.getData().add(new XYChart.Data<>(i, interestAccumulated));
        }

        lineChart.getData().add(series);
    }

    private void displayLoanDetails(double monthlyPayment, double totalPayment, double totalInterest) {
        monthlyPaymentText.setText("First Monthly Payment: " + String.format("%.2f", monthlyPayment));
        totalPaymentText.setText("Total Payment: " + String.format("%.2f", totalPayment));
        totalInterestText.setText("Total Interest: " + String.format("%.2f", totalInterest));
    }
}

class PaymentRow {
    private final String month;
    private final Double[] years;

    public PaymentRow(String month, int totalYears) {
        this.month = month;
        this.years = new Double[totalYears];
    }

    public String getMonth() {
        return month;
    }

    public Double getYear(int year) {
        return years[year - 1];
    }

    public void setYear(int year, Double payment) {
        this.years[year - 1] = payment;
    }
}