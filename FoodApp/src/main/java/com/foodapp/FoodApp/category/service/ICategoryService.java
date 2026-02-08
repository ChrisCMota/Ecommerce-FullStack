package com.foodapp.FoodApp.category.service;

import com.foodapp.FoodApp.category.dtos.CategoryDTO;
import com.foodapp.FoodApp.response.Response;

import java.util.List;

public interface ICategoryService {

    Response<CategoryDTO> addCategory(CategoryDTO categoryDTO);

    Response<CategoryDTO> updateCategory(CategoryDTO categoryDTO);

    Response<CategoryDTO> getCategoryById(Long id);

    Response<List<CategoryDTO>> getAllCategories();

    Response<?> deleteCategory(Long id);
}
