package co.castriq.saccoloan.repository;

import co.castriq.saccoloan.domain.Loan;
import co.castriq.saccoloan.domain.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findByMemberIdOrderByAppliedAtDesc(Long memberId);
    boolean existsByMemberIdAndStatus(Long memberId, LoanStatus status);
}
