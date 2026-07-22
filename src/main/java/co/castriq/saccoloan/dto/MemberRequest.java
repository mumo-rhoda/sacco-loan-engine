package co.castriq.saccoloan.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record MemberRequest(

        @NotBlank(message = "Full name is required")
        String fullName,

        @NotBlank(message = "Phone number is required")
        @Pattern(regexp = "^\\+254[0-9]{9}$", message = "Phone number must be in format +254XXXXXXXXX")
        String phoneNumber,

        @NotBlank(message = "National ID is required")
        String nationalId
) {}
