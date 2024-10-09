package loan;

public class Linear extends Loan {
    public Linear(double interest, double borrowed, int monthsToPay) {
        super(interest, borrowed, monthsToPay);
    }

    @Override
    public double getTotalPayment() {
        return borrowed + (borrowed * (interest / 100) * (monthsToPay / 12.0));
    }

    @Override
    public double getMonthlyPayment() {
        return borrowed / monthsToPay;
    }

    @Override
    public double getAccumulatedInterest(int month) {
        double monthlyInterestRate = interest / 100 / 12;
        return borrowed * monthlyInterestRate * month;
    }

    @Override
    public void calculateAndStoreMonthlyPayments() {
        monthlyPaymentsData = new double[monthsToPay];
        double principalPerMonth = getMonthlyPayment();
        double remainingBalance = borrowed;

        for (int month = 0; month < monthsToPay; month++) {
            double interestPayment = remainingBalance * (interest / 100 / 12);
            double monthlyPayment = principalPerMonth +  2* interestPayment;

            if (month == monthsToPay - 1)
                monthlyPayment = monthlyPayment - interestPayment;
            monthlyPaymentsData[month] = monthlyPayment;

            remainingBalance -= principalPerMonth;
        }
    }

}