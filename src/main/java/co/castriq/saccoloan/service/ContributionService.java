package co.castriq.saccoloan.service;

import co.castriq.saccoloan.domain.Contribution;
import co.castriq.saccoloan.domain.Member;
import co.castriq.saccoloan.dto.ContributionRequest;
import co.castriq.saccoloan.dto.ContributionResponse;
import co.castriq.saccoloan.repository.ContributionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContributionService {

    private final ContributionRepository contributionRepository;
    private final MemberService memberService;

    @Transactional
    public ContributionResponse recordContribution(Long memberId, ContributionRequest request) {
        Member member = memberService.findMemberOrThrow(memberId);

        Contribution contribution = Contribution.builder()
                .member(member)
                .amount(request.amount())
                .reference(request.reference())
                .build();

        Contribution saved = contributionRepository.save(contribution);
        return toResponse(saved);
    }

    public List<ContributionResponse> getContributionsForMember(Long memberId) {
        memberService.findMemberOrThrow(memberId);
        return contributionRepository.findByMemberIdOrderByContributedAtDesc(memberId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public BigDecimal getTotalContributions(Long memberId) {
        return contributionRepository.sumAmountByMemberId(memberId);
    }

    private ContributionResponse toResponse(Contribution contribution) {
        return new ContributionResponse(
                contribution.getId(),
                contribution.getMember().getId(),
                contribution.getAmount(),
                contribution.getContributedAt(),
                contribution.getReference()
        );
    }
}
