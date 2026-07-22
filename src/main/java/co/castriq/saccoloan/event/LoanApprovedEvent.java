package co.castriq.saccoloan.event;

import co.castriq.saccoloan.domain.Loan;
import org.springframework.context.ApplicationEvent;

/**
 * Fired once a loan clears the eligibility engine and is approved.
 * Decouples the decision (LoanService) from whatever happens next
 * (notification, disbursement, ledger posting, etc). In a production
 * SACCO system this is where you'd publish to Kafka/RabbitMQ or call
 * an M-Pesa B2C disbursement API instead of an in-process listener.
 */
public class LoanApprovedEvent extends ApplicationEvent {

    private final Loan loan;

    public LoanApprovedEvent(Object source, Loan loan) {
        super(source);
        this.loan = loan;
    }

    public Loan getLoan() {
        return loan;
    }
}
