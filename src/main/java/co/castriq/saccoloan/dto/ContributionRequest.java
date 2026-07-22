package co.castriq.saccoloan.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ContributionRequest(

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "1.0", message = "Contribution amount must be greater than zero")
        BigDecimal amount,

        String reference
) {}
