package loan;

public class Annuential extends Loan {
    public Annuential(double interest, double borrowed, int monthsToPay) {
        super(interest, borrowed, monthsToPay);
    }

    @Override
    public double getTotalPayment() {
        return borrowed + getAccumulatedInterest(getMonthsToPay());
    }

    @Override
    public double getMonthlyPayment() {
        double monthlyInterestRate = interest / 100 / 12;
        if (monthlyInterestRate == 0)
            return borrowed / monthsToPay;

        return (borrowed * monthlyInterestRate * Math.pow(1 + monthlyInterestRate, monthsToPay)) /
                (Math.pow(1 + monthlyInterestRate, monthsToPay) - 1);
    }

    @Override
    public double getAccumulatedInterest(int month) {
        double totalPaid = getMonthlyPayment() * month;
        double principalPaid = borrowed - calculateRemainingBalance(month);
        return totalPaid - principalPaid;
    }

    private double calculateRemainingBalance(int month) {
        double monthlyInterestRate = interest / 100 / 12;
        return borrowed * Math.pow(1 + monthlyInterestRate, month) -
                (getMonthlyPayment() * (Math.pow(1 + monthlyInterestRate, month) - 1)) / monthlyInterestRate;
    }

    @Override
    public void calculateAndStoreMonthlyPayments() {
        monthlyPaymentsData = new double[monthsToPay];
        double monthlyInterestRate = interest / 100 / 12;
        double remainingBalance = borrowed;

        for (int month = 0; month < monthsToPay; month++) {
            double interestPortion = remainingBalance * monthlyInterestRate;
            double principalPortion = remainingBalance / (monthsToPay - month);
            double monthlyPayment = principalPortion + interestPortion;

            monthlyPaymentsData[month] = monthlyPayment;

            remainingBalance -= principalPortion;

            if (month == monthsToPay - 1 && Math.abs(remainingBalance) < 0.01)
                remainingBalance = 0;

        }
    }
}