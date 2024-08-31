package com.shinhantime.tweione.Transaction.Repository;

import com.shinhantime.tweione.User.repository.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity(name = "transaction")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class TransactionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_user_id")
    private UserEntity fromUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_user_id")
    private UserEntity toUser;

    @Column(name = "amount", nullable = false)
    private Long amount;

    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate;

    @Column(name = "transaction_Hash")
    private String transactionHash;

    @PrePersist
    protected void onCreate() {
        this.transactionDate = LocalDateTime.now();
    }
}