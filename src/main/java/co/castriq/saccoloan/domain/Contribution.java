package co.castriq.saccoloan.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "contributions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Contribution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(name = "contributed_at", nullable = false)
    private LocalDateTime contributedAt;

    private String reference;

    @PrePersist
    void onCreate() {
        if (this.contributedAt == null) {
            this.contributedAt = LocalDateTime.now();
        }
    }
}
