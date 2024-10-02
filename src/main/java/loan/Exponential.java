package loan;

public class Exponential extends Loan {
    public Exponential(double interest, double borrowed, int monthsToPay) {
        super(interest, borrowed, monthsToPay);
    }

    @Override
    public double getTotalPayment() {
        return getMonthlyPayment() * monthsToPay;
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
        return borrowed * (Math.pow(1 + (interest / 100) / 12, month) - 1);
    }

    @Override
    public void calculateAndStoreMonthlyPayments() {
        double monthlyRate = (interest / 10) / 12;
        for (int month = 0; month < monthsToPay; month++) {
            double remainingPrincipal = borrowed * Math.pow(1 + monthlyRate, month);
            monthlyPaymentsData[month] = remainingPrincipal * monthlyRate;
        }
    }
}