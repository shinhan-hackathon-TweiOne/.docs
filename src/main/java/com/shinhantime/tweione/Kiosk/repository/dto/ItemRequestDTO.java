package com.shinhantime.tweione.Kiosk.repository.dto;

import lombok.Data;

@Data
public class ItemRequestDTO {
    private String itemName;
    private String categoryName;
    private String imageUrl;
    private Double price;
}