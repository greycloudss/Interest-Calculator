package loan;

public class Loan {
    protected double interest;
    protected double borrowed;
    protected int monthsToPay;
    protected double[] monthlyPaymentsData;

    Loan(double interest, double borrowed, int monthsToPay) {
        this.interest = interest;
        this.borrowed = borrowed;
        this.monthsToPay = monthsToPay;
        this.monthlyPaymentsData = new double[monthsToPay];
    }

    public double getTotalPayment() {
        return 0;
    }

    public double getMonthlyPayment() {

        return 0;
    }

    public void calculateAndStoreMonthlyPayments() {
        monthlyPaymentsData = new double[monthsToPay];
        for (int month = 0; month < monthsToPay; month++) {
            monthlyPaymentsData[month] = getMonthlyPayment();
        }
    }

    public int getMonthsToPay() {
        return monthsToPay;
    }

    public double[] getMonthlyPaymentsData() {
        return monthlyPaymentsData;
    }

    public double getAccumulatedInterest(int month) {
        return 0;
    }
}