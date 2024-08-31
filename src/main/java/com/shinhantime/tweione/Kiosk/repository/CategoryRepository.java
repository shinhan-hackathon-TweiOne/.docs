package com.shinhantime.tweione.Kiosk.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
    Optional<CategoryEntity> findByKioskIdAndName(Long kioskId, String name);
}