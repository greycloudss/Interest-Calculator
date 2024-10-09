/*
 **
 **
 */

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

    //Adding delay to payments start
    @FXML
    private CheckBox checkBox;

    @FXML
    private TextField frmTextBox;

    @FXML
    public TextField toTextBox;
    //Adding delay to payments end

    //Sorting Through payments start
    @FXML
    private CheckBox checkBox1;

    @FXML
    private TextField frmTextBox1;

    @FXML
    public TextField toTextBox1;
    //Sorting Through payments end

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
        configureCharts();
        monthlyPaymentText.setVisible(false);
        totalPaymentText.setVisible(false);
        totalInterestText.setVisible(false);
        tableA.setVisible(false);
    }

    private void initChoiceBox() {
        choiceBox.getItems().addAll("Linear", "Exponential", "Annuential");
        choiceBox.setValue("Linear");
    }

    private void configureCharts() {
        lineChart.setStyle("-fx-background-color: rgba(214,213,236,0);");
        xAxis.setTickLabelFill(Color.WHITE);
        yAxis.setTickLabelFill(Color.WHITE);
        tableA.setStyle("-fx-background-color: rgba(214,213,236,0); -fx-base: rgba(214,213,236,0.75);");
    }

    @FXML
    private void handleCloseButton() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleCalcButton() {
        tableA.getColumns().clear();
        String[] inputTexts = {zeroTextBox.getText(), oneTextBox.getText(), frmTextBox.getText(), toTextBox.getText(), frmTextBox1.getText(), toTextBox1.getText()};
        final double interest = 3.5;
        boolean checkboxState = checkBox.isSelected();
        boolean checkboxState1 = checkBox1.isSelected();

        int delayLength = 0;

        if (inputTexts[0].isEmpty() || inputTexts[1].isEmpty())
            return;


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
            double[] monthlyPayments = loan.getMonthlyPaymentsData();
            int totalMonths = monthlyPayments.length;

            if (checkboxState && !inputTexts[2].isEmpty() && !inputTexts[3].isEmpty()) {
                int frm = Integer.parseInt(inputTexts[2]) - 1;
                int to = Integer.parseInt(inputTexts[3]);

                if (frm <= to) {
                    monthlyPayments = slinkyArrays(loan.getMonthlyPaymentsData(), frm, to);
                    delayLength = Math.abs(to - frm);
                    totalMonths = monthlyPayments.length;
                }
            }

            int frmMonth = 0;
            int toMonth = totalMonths;

            if (checkboxState1 && !inputTexts[4].isEmpty() && !inputTexts[5].isEmpty()) {
                frmMonth = Integer.parseInt(inputTexts[4]) - 1;
                toMonth = Integer.parseInt(inputTexts[5]);
                if (frmMonth < 0) frmMonth = 0;
                if (toMonth > totalMonths) toMonth = totalMonths;
            }

            int monthsInRange = toMonth - frmMonth;
            int totalYears = (int) Math.ceil((double) monthsInRange / 12.0);

            monthColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getMonth()));
            tableA.getColumns().add(monthColumn);

            ObservableList<PaymentRow> paymentData = FXCollections.observableArrayList();

            double totalBalance = loan.getTotalPayment();

            int curMonth = 0;
            int year = 1;

            for (int monthIndex = frmMonth; monthIndex < toMonth; monthIndex++) {
                if (curMonth >= 12) {
                    curMonth = 0;
                    year++;
                }

                PaymentRow row;
                if (curMonth < paymentData.size()) {
                    row = paymentData.get(curMonth);
                } else {
                    row = new PaymentRow("Month " + (frmMonth + curMonth + 1), totalYears * 2);
                    paymentData.add(row);
                }

                double payment = monthlyPayments[monthIndex];
                row.setYear((year * 2) - 1, payment);
                row.setYear(year * 2, totalBalance);

                if (totalBalance - payment >= 0)
                    totalBalance -= payment;
                else
                    break;

                curMonth++;
            }

            tableA.getColumns().clear();
            tableA.getColumns().add(monthColumn);

            for (int i = 1; i <= year; i++) {
                TableColumn<PaymentRow, Double> paymentColumn = new TableColumn<>("Year " + i + " Payments");
                final int paymentIndex = (i * 2) - 1;
                paymentColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getYear(paymentIndex)));
                tableA.getColumns().add(paymentColumn);

                TableColumn<PaymentRow, Double> balanceColumn = new TableColumn<>("Loan Balance");
                final int balanceIndex = i * 2;
                balanceColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getYear(balanceIndex)));
                tableA.getColumns().add(balanceColumn);
            }

            tableA.setItems(paymentData);
            monthlyPaymentText.setVisible(true);
            totalPaymentText.setVisible(true);
            totalInterestText.setVisible(true);
            tableA.setVisible(true);

            populateData(totalMonths);
            displayLoanDetails(monthlyPayments[0], loan.getTotalPayment(), loan.getAccumulatedInterest(totalMonths - delayLength));

            calculationsDone++;

        } catch (NumberFormatException e) {
            System.err.println("Error parsing input: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private double[] filterPayments(double[] payments, int frm, int to) {
        int length = to - frm;
        double[] filtered = new double[length];
        System.arraycopy(payments, frm, filtered, 0, length);
        return filtered;
    }

    @FXML
    private void handleOutputBtn() throws IOException {
        if (calculationsDone <= 0)
            return;

        BufferedWriter writer = new BufferedWriter(new FileWriter("monthly_payments.txt"));

        int counter = 0;
        var total = loan.getTotalPayment();
        for (int i = 0; i < loan.getMonthsToPay(); ++i) {
            if (i % 12 == 0) {
                counter++;
                writer.write("\nyear " + counter + " payments\n");
            }
            writer.write(String.format("Month %d %.2f %.2f\n", (i % 12 + 1), loan.getMonthlyPaymentsData()[i], total -= loan.getMonthlyPaymentsData()[i]));
        }

        writer.close();
    }

    private void populateData(int months2Pay) {
        lineChart.getData().clear();
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName(choiceBox.getValue() + " Loan Interest");

        for (int i = 1; i <= months2Pay; i++) {
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

    private double[] slinkyArrays(double[] payments, int frm, int to) {
        int delay = Math.abs(frm - to);
        double[] result = new double[payments.length + delay];

        int resultIndex = 0;
        int paymentIndex = 0;

        while (paymentIndex < payments.length) {
            if (paymentIndex == frm) {
                for (int j = 0; j < delay; j++) {
                    if (resultIndex < result.length)
                        result[resultIndex++] = 0.0;

                }

            }
            if (resultIndex < result.length) {
                result[resultIndex++] = payments[paymentIndex];
            }
            paymentIndex++;
        }

        return result;
    }
}

class PaymentRow {
    private final String month;
    private final double[] years;

    public PaymentRow(String month, int totalYears) {
        this.month = month;
        this.years = new double[totalYears];
    }

    public String getMonth() {
        return month;
    }

    public double getYear(int year) {
        return years[year - 1];
    }

    public void setYear(int year, double payment) {
        this.years[year - 1] = (double) (Math.ceil(payment * 100) / 100);
    }
}