// F3 - caso bom: densidade de operadores baixa. Score esperado: 100.
public class F3Good {
    public double calculateCompoundInterest(double principal, double rate, int time) {
        double rateAsFraction = rate / 100;
        double base = 1 + rateAsFraction;
        double factor = Math.pow(base, time);
        double result = principal * factor;
        return result;
    }

    public double calculateMonthlyPayment(double loanAmount, double annualRate, int months) {
        double monthlyRate = annualRate / 12;
        double rateFactor = monthlyRate / 100;
        double onePlusR = 1 + rateFactor;
        double powerTerm = Math.pow(onePlusR, months);
        double numerator = loanAmount * rateFactor;
        numerator = numerator * powerTerm;
        double denominator = powerTerm - 1;
        double payment = numerator / denominator;
        return payment;
    }

    public boolean isEligibleForLoan(double creditScore, double debtToIncomeRatio, double yearsAtJob) {
        boolean hasGoodCredit = creditScore > 700;
        boolean hasLowDebt = debtToIncomeRatio < 0.4;
        boolean isStable = yearsAtJob >= 2;
        
        if (hasGoodCredit && hasLowDebt) {
            return isStable;
        }
        return false;
    }
}
