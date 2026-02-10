package com.foodapp.FoodApp.menu.service;

import com.foodapp.FoodApp.menu.dtos.MenuDTO;
import com.foodapp.FoodApp.response.Response;

import java.util.List;

public interface IMenuService {

    Response<MenuDTO> createMenu(MenuDTO menuDTO);

    Response<MenuDTO> updateMenu(MenuDTO menuDTO);

    Response<MenuDTO> getMenuById(Long id);

    Response<?> deleteMenu(Long id);

    Response<List<MenuDTO>> getMenus(Long categoryId, String search);
}
