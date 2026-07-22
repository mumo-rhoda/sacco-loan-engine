package co.castriq.saccoloan.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Simulates a downstream disbursement notification. Runs asynchronously
 * so a slow notification channel (SMS/M-Pesa gateway) never blocks the
 * HTTP response to the loan officer or member.
 */
@Slf4j
@Component
public class LoanApprovedEventListener {

    @Async
    @EventListener
    public void onLoanApproved(LoanApprovedEvent event) {
        log.info("Disbursement initiated for loan id={}, member id={}, amount={}",
                event.getLoan().getId(),
                event.getLoan().getMember().getId(),
                event.getLoan().getRequestedAmount());
        // Placeholder for real integration: M-Pesa B2C API call, SMS gateway, etc.
    }
}
