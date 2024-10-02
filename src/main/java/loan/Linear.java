package loan;

public class Linear extends Loan {
    public Linear(double interest, double borrowed, int monthsToPay) {
        super(interest, borrowed, monthsToPay);
    }

    @Override
    public double getTotalPayment() {
        return borrowed + (borrowed * (interest / 100) * monthsToPay);
    }

    @Override
    public double getMonthlyPayment() {
        return getTotalPayment() / monthsToPay;
    }

    @Override
    public double getAccumulatedInterest(int month) {
        return borrowed * (interest / 100) * month;
    }

    @Override
    public void calculateAndStoreMonthlyPayments() {
        double principalPerMonth = borrowed / monthsToPay;
        for (int month = 0; month < monthsToPay; month++) {
            double interestPayment = (borrowed - (principalPerMonth * month)) * (interest / 100 / 12);
            monthlyPaymentsData[month] = principalPerMonth + interestPayment;
        }
    }
}