package com.foodapp.FoodApp.menu.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.foodapp.FoodApp.DTOValidation.ValidationGroups;
import com.foodapp.FoodApp.review.dtos.ReviewDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MenuDTO {

    private Long id;

    @NotBlank(message = "Name is required", groups = ValidationGroups.OnCreate.class)
    private String name;

    private String description;

    @NotNull(message = "Price is required", groups = ValidationGroups.OnCreate.class)
    @Positive(message = "Price must be positive", groups = ValidationGroups.OnCreate.class)
    private BigDecimal price;

    private String imageUrl;

    @NotNull(message = "Category ID id required", groups = ValidationGroups.OnCreate.class)
    private Long categoryId; // needed when adding a menu (What category the menu belongs to)

    private MultipartFile imageFile; // For uploading the image

    private List<ReviewDTO> reviews;
}
