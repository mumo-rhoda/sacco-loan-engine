package co.castriq.saccoloan.service;

public record LoanEligibilityResult(boolean eligible, String reason) {

    public static LoanEligibilityResult approve(String reason) {
        return new LoanEligibilityResult(true, reason);
    }

    public static LoanEligibilityResult reject(String reason) {
        return new LoanEligibilityResult(false, reason);
    }
}
