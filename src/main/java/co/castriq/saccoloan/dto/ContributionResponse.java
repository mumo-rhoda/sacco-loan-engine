package co.castriq.saccoloan.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ContributionResponse(
        Long id,
        Long memberId,
        BigDecimal amount,
        LocalDateTime contributedAt,
        String reference
) {}
