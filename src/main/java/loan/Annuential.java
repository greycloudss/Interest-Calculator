package loan;

public class Annuential extends Loan {
    public Annuential(double interest, double borrowed, int monthsToPay) {
        super(interest, borrowed, monthsToPay);
    }

    @Override
    public double getTotalPayment() {
        return getMonthlyPayment() * monthsToPay;
    }

    @Override
    public double getMonthlyPayment() {
        double monthlyInterestRate = interest / 100 / 12;
        if (monthlyInterestRate == 0) {
            return borrowed / monthsToPay;
        }
        return (borrowed * monthlyInterestRate * Math.pow(1 + monthlyInterestRate, monthsToPay)) /
                (Math.pow(1 + monthlyInterestRate, monthsToPay) - 1);
    }

    @Override
    public double getAccumulatedInterest(int month) {
        double monthlyInterestRate = interest / 100 / 12;
        return (borrowed * (Math.pow(1 + monthlyInterestRate, month) - 1));
    }

    @Override
    public void calculateAndStoreMonthlyPayments() {
        double monthlyPayment = getMonthlyPayment();
        for (int month = 0; month < monthsToPay; month++) {
            monthlyPaymentsData[month] = monthlyPayment;
        }
    }
}
