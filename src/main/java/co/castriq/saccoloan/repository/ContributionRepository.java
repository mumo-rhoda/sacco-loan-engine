package co.castriq.saccoloan.repository;

import co.castriq.saccoloan.domain.Contribution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface ContributionRepository extends JpaRepository<Contribution, Long> {

    List<Contribution> findByMemberIdOrderByContributedAtDesc(Long memberId);

    @Query("select coalesce(sum(c.amount), 0) from Contribution c where c.member.id = :memberId")
    BigDecimal sumAmountByMemberId(@Param("memberId") Long memberId);
}
