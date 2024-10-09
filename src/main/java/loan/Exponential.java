package loan;

public class Exponential extends Loan {
    public Exponential(double interest, double borrowed, int monthsToPay) {
        super(interest, borrowed, monthsToPay);
    }

    @Override
    public double getTotalPayment() {
        return borrowed + getAccumulatedInterest(getMonthsToPay());
    }

    @Override
    public double getMonthlyPayment() {
        double monthlyRate = (interest / 100) / 12;
        if (monthlyRate == 0) {
            return borrowed / monthsToPay;
        }
        return (borrowed * monthlyRate * Math.pow(1 + monthlyRate, monthsToPay)) /
                (Math.pow(1 + monthlyRate, monthsToPay) - 1);
    }

    @Override
    public double getAccumulatedInterest(int month) {
        double monthlyRate = interest / 100 / 12;
        return borrowed * (Math.pow(1 + monthlyRate, month) - 1);
    }

    @Override
    public void calculateAndStoreMonthlyPayments() {
        monthlyPaymentsData = new double[monthsToPay];
        double monthlyRate = interest / 100 / 12;
        double remainingBalance = borrowed;

        for (int month = 0; month < monthsToPay; month++) {
            double interestPayment = remainingBalance * monthlyRate;
            double principalPayment = remainingBalance / (monthsToPay - month);
            double monthlyPayment = principalPayment + interestPayment;

            monthlyPaymentsData[month] = monthlyPayment;

            remainingBalance -= principalPayment;

            if (month == monthsToPay - 1 && Math.abs(remainingBalance) < 0.01) {
                remainingBalance = 0;
            }
        }
    }
}