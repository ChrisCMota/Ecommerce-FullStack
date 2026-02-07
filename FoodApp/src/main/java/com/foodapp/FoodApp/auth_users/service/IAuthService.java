package com.foodapp.FoodApp.auth_users.service;

import com.foodapp.FoodApp.auth_users.dtos.LoginRequest;
import com.foodapp.FoodApp.auth_users.dtos.RegistrationRequest;
import com.foodapp.FoodApp.response.Response;

public interface IAuthService {

    Response<?> register(RegistrationRequest registrationRequest);
    Response<?> login(LoginRequest loginRequest);
}
