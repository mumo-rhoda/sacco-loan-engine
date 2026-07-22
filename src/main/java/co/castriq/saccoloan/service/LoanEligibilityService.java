package co.castriq.saccoloan.service;

import co.castriq.saccoloan.domain.LoanStatus;
import co.castriq.saccoloan.domain.Member;
import co.castriq.saccoloan.repository.ContributionRepository;
import co.castriq.saccoloan.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;

/**
 * Encapsulates SACCO loan eligibility rules as a small, testable rule chain.
 * Each rule is evaluated in order and short-circuits on the first failure,
 * mirroring how a real SACCO credit committee would reason through an
 * application: tenure first, then savings adequacy, then exposure, then
 * the multiplier cap.
 *
 * Rules applied (defaults, configurable via application.yml):
 *   1. Member must have been active for at least sacco.eligibility.minimum-membership-months
 *   2. Total contributions must be at least sacco.eligibility.minimum-total-contribution
 *   3. Member must not already have a PENDING or APPROVED loan
 *   4. Requested amount must not exceed (total contributions x sacco.eligibility.loan-multiplier)
 */
@Service
@RequiredArgsConstructor
public class LoanEligibilityService {

    private final ContributionRepository contributionRepository;
    private final LoanRepository loanRepository;

    @Value("${sacco.eligibility.minimum-membership-months}")
    private int minimumMembershipMonths;

    @Value("${sacco.eligibility.minimum-total-contribution}")
    private BigDecimal minimumTotalContribution;

    @Value("${sacco.eligibility.loan-multiplier}")
    private int loanMultiplier;

    public LoanEligibilityResult evaluate(Member member, BigDecimal requestedAmount) {

        int membershipMonths = Period.between(member.getJoinedDate(), LocalDate.now()).getMonths()
                + Period.between(member.getJoinedDate(), LocalDate.now()).getYears() * 12;

        if (membershipMonths < minimumMembershipMonths) {
            return LoanEligibilityResult.reject(
                    "Member has been active for " + membershipMonths + " month(s); minimum required is "
                            + minimumMembershipMonths);
        }

        BigDecimal totalContributions = contributionRepository.sumAmountByMemberId(member.getId());

        if (totalContributions.compareTo(minimumTotalContribution) < 0) {
            return LoanEligibilityResult.reject(
                    "Total contributions of KES " + totalContributions
                            + " are below the minimum required KES " + minimumTotalContribution);
        }

        boolean hasActiveLoan = loanRepository.existsByMemberIdAndStatus(member.getId(), LoanStatus.PENDING)
                || loanRepository.existsByMemberIdAndStatus(member.getId(), LoanStatus.APPROVED);

        if (hasActiveLoan) {
            return LoanEligibilityResult.reject("Member already has a pending or approved loan");
        }

        BigDecimal maxEligible = totalContributions.multiply(BigDecimal.valueOf(loanMultiplier));

        if (requestedAmount.compareTo(maxEligible) > 0) {
            return LoanEligibilityResult.reject(
                    "Requested amount KES " + requestedAmount + " exceeds maximum eligible KES " + maxEligible
                            + " (" + loanMultiplier + "x total contributions)");
        }

        return LoanEligibilityResult.approve(
                "Approved: within " + loanMultiplier + "x contribution multiplier, tenure and contribution checks passed");
    }
}
