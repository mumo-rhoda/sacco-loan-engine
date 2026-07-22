package co.castriq.saccoloan.service;

import co.castriq.saccoloan.domain.Member;
import co.castriq.saccoloan.dto.MemberRequest;
import co.castriq.saccoloan.dto.MemberResponse;
import co.castriq.saccoloan.exception.DuplicateMemberException;
import co.castriq.saccoloan.exception.ResourceNotFoundException;
import co.castriq.saccoloan.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public MemberResponse registerMember(MemberRequest request) {
        if (memberRepository.existsByPhoneNumber(request.phoneNumber())) {
            throw new DuplicateMemberException("A member with this phone number already exists");
        }
        if (memberRepository.existsByNationalId(request.nationalId())) {
            throw new DuplicateMemberException("A member with this national ID already exists");
        }

        Member member = Member.builder()
                .memberNumber(generateMemberNumber())
                .fullName(request.fullName())
                .phoneNumber(request.phoneNumber())
                .nationalId(request.nationalId())
                .joinedDate(LocalDate.now())
                .build();

        Member saved = memberRepository.save(member);
        return toResponse(saved);
    }

    public MemberResponse getMember(Long id) {
        return toResponse(findMemberOrThrow(id));
    }

    public Member findMemberOrThrow(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with id " + id));
    }

    private String generateMemberNumber() {
        return "SACCO-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private MemberResponse toResponse(Member member) {
        return new MemberResponse(
                member.getId(),
                member.getMemberNumber(),
                member.getFullName(),
                member.getPhoneNumber(),
                member.getJoinedDate()
        );
    }
}
