package com.shinhantime.tweione.UserKeyEntity.Repository;


import com.shinhantime.tweione.User.repository.UserEntity;
import jakarta.persistence.*;
import lombok.*;

@Setter
@Entity(name = "user_key")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserKeyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_key")
    private String userKey;

}
