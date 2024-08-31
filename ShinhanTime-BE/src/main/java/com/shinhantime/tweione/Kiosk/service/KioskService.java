package com.shinhantime.tweione.Kiosk.service;

import com.shinhantime.tweione.Kiosk.repository.*;
import com.shinhantime.tweione.Kiosk.repository.dto.CategoryDTO;
import com.shinhantime.tweione.Kiosk.repository.dto.ItemRequestDTO;
import com.shinhantime.tweione.User.repository.UserEntity;
import com.shinhantime.tweione.User.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class KioskService {
    private final KioskRepository kioskRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ItemRepository itemRepository;

    public KioskService(KioskRepository kioskRepository, UserRepository userRepository,
                        CategoryRepository categoryRepository, ItemRepository itemRepository) {
        this.kioskRepository = kioskRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.itemRepository = itemRepository;
    }

    public List<CategoryDTO> getKioskCategoriesByUserId(Long userId) {
        return kioskRepository.findByUserId(userId)
                .map(kiosk -> kiosk.getCategories().stream()
                        .map(CategoryDTO::fromEntity)
                        .collect(Collectors.toList()))
                .orElse(null); // 키오스크가 없으면 null 반환
    }

    public void addItemToKiosk(Long userId, ItemRequestDTO itemRequestDTO) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        KioskEntity kiosk = user.getKiosk();
        if (kiosk == null) {
            kiosk = KioskEntity.builder().user(user).build();
            kioskRepository.save(kiosk);
            user.setKiosk(kiosk);
            userRepository.save(user);
        }

        KioskEntity finalKiosk = kiosk; // Final or effectively final variable
        CategoryEntity category = categoryRepository.findByKioskIdAndName(finalKiosk.getId(), itemRequestDTO.getCategoryName())
                .orElseGet(() -> {
                    CategoryEntity newCategory = CategoryEntity.builder()
                            .name(itemRequestDTO.getCategoryName())
                            .kiosk(finalKiosk)
                            .build();
                    return categoryRepository.save(newCategory);
                });

        ItemEntity item = ItemEntity.builder()
                .name(itemRequestDTO.getItemName())
                .imageUrl(itemRequestDTO.getImageUrl())
                .price(itemRequestDTO.getPrice())
                .category(category)
                .build();

        itemRepository.save(item);
    }
}