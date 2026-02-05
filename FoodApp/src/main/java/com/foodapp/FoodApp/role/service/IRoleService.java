package com.foodapp.FoodApp.role.service;

import com.foodapp.FoodApp.response.Response;
import com.foodapp.FoodApp.role.dtos.RoleDTO;

import java.util.List;

public interface IRoleService {

    Response<RoleDTO> createRole(RoleDTO roleDTO);

    Response<RoleDTO> updateRole(RoleDTO roleDTO);

    Response<?> deleteRole(Long id);

    Response<List<RoleDTO>> getAllRoles();
}
