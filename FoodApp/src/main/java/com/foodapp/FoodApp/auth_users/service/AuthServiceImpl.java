package com.foodapp.FoodApp.auth_users.service;

import com.foodapp.FoodApp.auth_users.dtos.LoginRequest;
import com.foodapp.FoodApp.auth_users.dtos.LoginResponse;
import com.foodapp.FoodApp.auth_users.dtos.RegistrationRequest;
import com.foodapp.FoodApp.auth_users.entity.User;
import com.foodapp.FoodApp.auth_users.repository.UserRepository;
import com.foodapp.FoodApp.exceptions.BadRequestException;
import com.foodapp.FoodApp.exceptions.NotFoundException;
import com.foodapp.FoodApp.response.Response;
import com.foodapp.FoodApp.role.entity.Role;
import com.foodapp.FoodApp.role.repository.RoleRepository;
import com.foodapp.FoodApp.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements IAuthService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final RoleRepository roleRepository;

    @Override
    public Response<?> register(RegistrationRequest registrationRequest) {
        log.info("INSIDE register()");

        if(userRepository.existsByEmail(registrationRequest.getEmail())){
            throw new BadRequestException("Email Already exists");
        }

        //collect all roles from the request
        List<Role> userRoles;
        if(registrationRequest.getRoles() != null && !registrationRequest.getRoles().isEmpty()){
            userRoles = registrationRequest.getRoles().stream()
                    .map(roleName -> roleRepository.findByName(roleName.toUpperCase())
                            .orElseThrow(()-> new NotFoundException("Role with name: " + roleName + "Not found")))
                    .toList();
        }else{
            //if no roles provided, default to customer
            Role defaultRole = roleRepository.findByName("CUSTOMER")
                    .orElseThrow(() -> new NotFoundException("Default CUSTOMER role not found"));
            userRoles = List.of(defaultRole);
        }

        //build user obj
        User userToSave = User.builder()
                .name(registrationRequest.getName())
                .email(registrationRequest.getEmail())
                .password(passwordEncoder.encode(registrationRequest.getPassword()))
                .address(registrationRequest.getAddress())
                .phoneNumber(registrationRequest.getPhoneNumber())
                .roles(userRoles)
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .build();

        //Save the user
        userRepository.save(userToSave);

        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("User registered Successfully")
                .build();

    }

    @Override
    public Response<LoginResponse> login(LoginRequest loginRequest) {
        log.info("INSIDE login()");

        //check user email(login)
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new NotFoundException("Invalid Email"));

        if(!user.isActive()){
            throw new NotFoundException("Account not active, Please contact customer support");
        }

        //verify password
        if(!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())){
            throw new BadRequestException("Invalid Password");
        }

        //Generate Token
        String token = jwtUtils.generateToken(user.getEmail());

        //Extract roles as a list
        List<String> roleNames = user.getRoles().stream()
                .map(Role::getName)
                .toList();

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(token);
        loginResponse.setRoles(roleNames);

        return Response.<LoginResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Login Successful")
                .data(loginResponse)
                .build();
    }
}
