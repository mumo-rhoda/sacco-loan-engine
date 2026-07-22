package co.castriq.saccoloan.controller;

import co.castriq.saccoloan.dto.LoanApplicationRequest;
import co.castriq.saccoloan.dto.LoanResponse;
import co.castriq.saccoloan.service.LoanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
@Tag(name = "Loans", description = "Loan applications and eligibility decisions")
public class LoanController {

    private final LoanService loanService;

    @PostMapping("/apply")
    @Operation(summary = "Apply for a loan; evaluated synchronously against the eligibility engine")
    public ResponseEntity<LoanResponse> apply(@Valid @RequestBody LoanApplicationRequest request) {
        return ResponseEntity.ok(loanService.applyForLoan(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Fetch a loan decision by id")
    public ResponseEntity<LoanResponse> getLoan(@PathVariable Long id) {
        return ResponseEntity.ok(loanService.getLoan(id));
    }

    @GetMapping("/members/{memberId}")
    @Operation(summary = "List all loan applications for a member")
    public ResponseEntity<List<LoanResponse>> listForMember(@PathVariable Long memberId) {
        return ResponseEntity.ok(loanService.getLoansForMember(memberId));
    }
}
