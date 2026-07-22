package co.castriq.saccoloan.service;

import co.castriq.saccoloan.domain.Loan;
import co.castriq.saccoloan.domain.LoanStatus;
import co.castriq.saccoloan.domain.Member;
import co.castriq.saccoloan.dto.LoanApplicationRequest;
import co.castriq.saccoloan.dto.LoanResponse;
import co.castriq.saccoloan.event.LoanApprovedEvent;
import co.castriq.saccoloan.exception.ResourceNotFoundException;
import co.castriq.saccoloan.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanService {

    private final LoanRepository loanRepository;
    private final MemberService memberService;
    private final LoanEligibilityService eligibilityService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public LoanResponse applyForLoan(LoanApplicationRequest request) {
        Member member = memberService.findMemberOrThrow(request.memberId());

        LoanEligibilityResult result = eligibilityService.evaluate(member, request.requestedAmount());

        Loan loan = Loan.builder()
                .member(member)
                .requestedAmount(request.requestedAmount())
                .status(result.eligible() ? LoanStatus.APPROVED : LoanStatus.REJECTED)
                .decisionReason(result.reason())
                .decidedAt(LocalDateTime.now())
                .build();

        Loan saved = loanRepository.save(loan);

        if (result.eligible()) {
            eventPublisher.publishEvent(new LoanApprovedEvent(this, saved));
        }

        return toResponse(saved);
    }

    public LoanResponse getLoan(Long id) {
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found with id " + id));
        return toResponse(loan);
    }

    public List<LoanResponse> getLoansForMember(Long memberId) {
        memberService.findMemberOrThrow(memberId);
        return loanRepository.findByMemberIdOrderByAppliedAtDesc(memberId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private LoanResponse toResponse(Loan loan) {
        return new LoanResponse(
                loan.getId(),
                loan.getMember().getId(),
                loan.getRequestedAmount(),
                loan.getStatus(),
                loan.getDecisionReason(),
                loan.getAppliedAt(),
                loan.getDecidedAt()
        );
    }
}
