package com.shinhantime.tweione.Kiosk.repository.dto;


import com.shinhantime.tweione.Kiosk.repository.ItemEntity;
import lombok.Data;

@Data
public class ItemDTO {
    private Long id;
    private String name;
    private String imageUrl;
    private Double price;

    public static ItemDTO fromEntity(ItemEntity itemEntity) {
        ItemDTO dto = new ItemDTO();
        dto.setId(itemEntity.getId());
        dto.setName(itemEntity.getName());
        dto.setImageUrl(itemEntity.getImageUrl());
        dto.setPrice(itemEntity.getPrice());
        return dto;
    }
}