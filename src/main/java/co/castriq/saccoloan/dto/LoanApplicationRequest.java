package co.castriq.saccoloan.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record LoanApplicationRequest(

        @NotNull(message = "Member id is required")
        Long memberId,

        @NotNull(message = "Requested amount is required")
        @DecimalMin(value = "1.0", message = "Requested amount must be greater than zero")
        BigDecimal requestedAmount
) {}
