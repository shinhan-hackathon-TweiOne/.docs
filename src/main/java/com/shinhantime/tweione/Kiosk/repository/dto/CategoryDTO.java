package com.shinhantime.tweione.Kiosk.repository.dto;



import com.shinhantime.tweione.Kiosk.repository.CategoryEntity;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class CategoryDTO {
    private Long id;
    private String name;
    private List<ItemDTO> items;

    public static CategoryDTO fromEntity(CategoryEntity categoryEntity) {
        CategoryDTO dto = new CategoryDTO();
        dto.setId(categoryEntity.getId());
        dto.setName(categoryEntity.getName());
        dto.setItems(categoryEntity.getItems().stream()
                .map(ItemDTO::fromEntity)
                .collect(Collectors.toList()));
        return dto;
    }
}