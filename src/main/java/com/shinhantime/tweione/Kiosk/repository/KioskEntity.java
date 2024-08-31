package com.shinhantime.tweione.Kiosk.repository;

import com.shinhantime.tweione.User.repository.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity(name = "kiosk")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class KioskEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @OneToMany(mappedBy = "kiosk", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CategoryEntity> categories;
}