package co.castriq.saccoloan.dto;

import java.time.LocalDate;

public record MemberResponse(
        Long id,
        String memberNumber,
        String fullName,
        String phoneNumber,
        LocalDate joinedDate
) {}
