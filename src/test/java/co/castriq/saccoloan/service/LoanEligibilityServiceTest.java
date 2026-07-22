package co.castriq.saccoloan.service;

import co.castriq.saccoloan.domain.LoanStatus;
import co.castriq.saccoloan.domain.Member;
import co.castriq.saccoloan.repository.ContributionRepository;
import co.castriq.saccoloan.repository.LoanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoanEligibilityServiceTest {

    @Mock
    private ContributionRepository contributionRepository;

    @Mock
    private LoanRepository loanRepository;

    @InjectMocks
    private LoanEligibilityService eligibilityService;

    private Member member;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(eligibilityService, "minimumMembershipMonths", 3);
        ReflectionTestUtils.setField(eligibilityService, "minimumTotalContribution", BigDecimal.valueOf(5000));
        ReflectionTestUtils.setField(eligibilityService, "loanMultiplier", 3);

        member = Member.builder()
                .id(1L)
                .fullName("Wanjiru Kamau")
                .joinedDate(LocalDate.now().minusMonths(6))
                .build();
    }

    @Test
    void rejectsWhenMembershipTooShort() {
        member.setJoinedDate(LocalDate.now().minusMonths(1));

        LoanEligibilityResult result = eligibilityService.evaluate(member, BigDecimal.valueOf(1000));

        assertThat(result.eligible()).isFalse();
        assertThat(result.reason()).contains("minimum required is 3");
    }

    @Test
    void rejectsWhenContributionsBelowMinimum() {
        when(contributionRepository.sumAmountByMemberId(1L)).thenReturn(BigDecimal.valueOf(2000));

        LoanEligibilityResult result = eligibilityService.evaluate(member, BigDecimal.valueOf(1000));

        assertThat(result.eligible()).isFalse();
        assertThat(result.reason()).contains("below the minimum required");
    }

    @Test
    void rejectsWhenMemberHasActiveLoan() {
        when(contributionRepository.sumAmountByMemberId(1L)).thenReturn(BigDecimal.valueOf(10000));
        when(loanRepository.existsByMemberIdAndStatus(1L, LoanStatus.PENDING)).thenReturn(true);

        LoanEligibilityResult result = eligibilityService.evaluate(member, BigDecimal.valueOf(1000));

        assertThat(result.eligible()).isFalse();
        assertThat(result.reason()).contains("already has a pending or approved loan");
    }

    @Test
    void rejectsWhenRequestExceedsMultiplier() {
        when(contributionRepository.sumAmountByMemberId(1L)).thenReturn(BigDecimal.valueOf(10000));
        when(loanRepository.existsByMemberIdAndStatus(any(), any())).thenReturn(false);

        LoanEligibilityResult result = eligibilityService.evaluate(member, BigDecimal.valueOf(40000));

        assertThat(result.eligible()).isFalse();
        assertThat(result.reason()).contains("exceeds maximum eligible");
    }

    @Test
    void approvesWhenAllRulesPass() {
        when(contributionRepository.sumAmountByMemberId(1L)).thenReturn(BigDecimal.valueOf(10000));
        when(loanRepository.existsByMemberIdAndStatus(any(), any())).thenReturn(false);

        LoanEligibilityResult result = eligibilityService.evaluate(member, BigDecimal.valueOf(25000));

        assertThat(result.eligible()).isTrue();
        assertThat(result.reason()).contains("Approved");
    }
}
