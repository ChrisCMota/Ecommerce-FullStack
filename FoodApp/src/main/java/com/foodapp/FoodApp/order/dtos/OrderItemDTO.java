package com.foodapp.FoodApp.order.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.foodapp.FoodApp.menu.dtos.MenuDTO;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderItemDTO {

    private Long id;

    private int quantity;

    private Long menuId;

    private MenuDTO menu;

    private BigDecimal pricePerUnit;

    private BigDecimal subtotal;
}
