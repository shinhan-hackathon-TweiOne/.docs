package com.shinhantime.tweione.Kiosk.controller;

import com.shinhantime.tweione.Kiosk.repository.dto.CategoryDTO;
import com.shinhantime.tweione.Kiosk.repository.dto.ItemRequestDTO;
import com.shinhantime.tweione.Kiosk.service.KioskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/kiosk")
public class KioskController {

    private final KioskService kioskService;

    public KioskController(KioskService kioskService) {
        this.kioskService = kioskService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<CategoryDTO>> getKioskCategoriesByUserId(@PathVariable Long userId) {
        List<CategoryDTO> categories = kioskService.getKioskCategoriesByUserId(userId);
        return ResponseEntity.ok(categories);
    }

    @PostMapping("/{userId}")
    public ResponseEntity<String> addItemToKiosk(@PathVariable Long userId, @RequestBody ItemRequestDTO itemRequestDTO) {
        kioskService.addItemToKiosk(userId, itemRequestDTO);
        return ResponseEntity.ok("Item added successfully");
    }
}