package com.foodapp.FoodApp.auth_users.service;

import com.foodapp.FoodApp.auth_users.dtos.UserDTO;
import com.foodapp.FoodApp.auth_users.entity.User;
import com.foodapp.FoodApp.response.Response;

import java.util.List;

public interface IUserService {

    User getCurrentLoggedInUser();

    Response<List<UserDTO>> getAllUsers();

    Response<UserDTO> getOwnAccountDetails();

    Response<?> updateOwnAccount(UserDTO userDTO );

    Response<?> deactivateOwnAccount();
}
