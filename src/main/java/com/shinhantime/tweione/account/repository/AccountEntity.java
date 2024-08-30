package com.shinhantime.tweione.account.repository;

import com.shinhantime.tweione.User.repository.UserEntity;
import jakarta.persistence.*;
import lombok.*;


@Setter
@Entity(name = "account")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class AccountEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_number")
    private String accountNumber;

    @Column(name = "bank_name")
    private String bankName;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    // Getters and Setters
}
