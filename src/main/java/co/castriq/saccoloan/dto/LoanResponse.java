package co.castriq.saccoloan.dto;

import co.castriq.saccoloan.domain.LoanStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record LoanResponse(
        Long id,
        Long memberId,
        BigDecimal requestedAmount,
        LoanStatus status,
        String decisionReason,
        LocalDateTime appliedAt,
        LocalDateTime decidedAt
) {}
