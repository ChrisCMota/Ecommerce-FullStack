package com.foodapp.FoodApp.menu.controller;

import com.foodapp.FoodApp.DTOValidation.ValidationGroups;
import com.foodapp.FoodApp.menu.dtos.MenuDTO;
import com.foodapp.FoodApp.menu.service.IMenuService;
import com.foodapp.FoodApp.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/menu")
public class MenuController {

    private final IMenuService menuService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response<MenuDTO>> createMenu(@ModelAttribute @Validated(ValidationGroups.OnCreate.class) MenuDTO menuDTO,
                                                        @RequestPart(value = "imageFile", required = true)MultipartFile imageFile){

        // @ModelAttribute binds simple form fields to the DTO, while @RequestPart extracts the file separately.
        // If the DTO already has a MultipartFile field with the same name, Spring can bind it automatically,
        // and manual setting is not necessary. But I choose to that this way.
        menuDTO.setImageFile(imageFile);

        return ResponseEntity.ok(menuService.createMenu(menuDTO));
    }

    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response<MenuDTO>> updateMenu(@ModelAttribute @Validated(ValidationGroups.OnUpdate.class) MenuDTO menuDTO,
                                                        @RequestPart(value = "imageFile", required = false)MultipartFile imageFile){

        //The same as above explanation
        menuDTO.setImageFile(imageFile);

        return ResponseEntity.ok(menuService.updateMenu(menuDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response<MenuDTO>> getMenuById(@PathVariable Long id){

        return ResponseEntity.ok(menuService.getMenuById(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response<?>> deleteMenu(@PathVariable Long id){

        return ResponseEntity.ok(menuService.deleteMenu(id));
    }

    @GetMapping
    public ResponseEntity<Response<List<MenuDTO>>> getMenus(@RequestParam(required = false) Long id,
                                                            @RequestParam(required = false) String search){

        return ResponseEntity.ok(menuService.getMenus(id, search));
    }

}
