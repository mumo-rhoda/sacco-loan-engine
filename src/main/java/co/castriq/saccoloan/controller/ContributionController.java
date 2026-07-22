package co.castriq.saccoloan.controller;

import co.castriq.saccoloan.dto.ContributionRequest;
import co.castriq.saccoloan.dto.ContributionResponse;
import co.castriq.saccoloan.service.ContributionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/members/{memberId}/contributions")
@RequiredArgsConstructor
@Tag(name = "Contributions", description = "Member savings contributions")
public class ContributionController {

    private final ContributionService contributionService;

    @PostMapping
    @Operation(summary = "Record a savings contribution for a member")
    public ResponseEntity<ContributionResponse> record(@PathVariable Long memberId,
                                                         @Valid @RequestBody ContributionRequest request) {
        ContributionResponse response = contributionService.recordContribution(memberId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "List contributions for a member")
    public ResponseEntity<List<ContributionResponse>> list(@PathVariable Long memberId) {
        return ResponseEntity.ok(contributionService.getContributionsForMember(memberId));
    }
}
