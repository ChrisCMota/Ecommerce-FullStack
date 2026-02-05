package com.foodapp.FoodApp.role.service;

import com.foodapp.FoodApp.exceptions.BadRequestException;
import com.foodapp.FoodApp.exceptions.NotFoundException;
import com.foodapp.FoodApp.response.Response;
import com.foodapp.FoodApp.role.dtos.RoleDTO;
import com.foodapp.FoodApp.role.entity.Role;
import com.foodapp.FoodApp.role.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoleServiceImpl implements IRoleService {

    private final RoleRepository roleRepository;
    private final ModelMapper modelMapper;

    @Override
    public Response<RoleDTO> createRole(RoleDTO roleDTO) {

        if(roleRepository.findByName(roleDTO.getName()).isPresent()){
            throw new BadRequestException("Role with that name already exists");
        }

        Role newRole = modelMapper.map(roleDTO, Role.class);

        Role savedRole = roleRepository.save(newRole);

        return Response.<RoleDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Role created successfully")
                .data(modelMapper.map(savedRole, RoleDTO.class))
                .build();
    }

    @Override
    public Response<RoleDTO> updateRole(RoleDTO roleDTO) {

        Role existingRole = roleRepository.findById(roleDTO.getId())
                .orElseThrow(()-> new NotFoundException("Role not found"));

        if(roleRepository.findByName(roleDTO.getName()).isPresent()){
            throw new BadRequestException("Role with that name already exists");
        }

        existingRole.setName(roleDTO.getName());

        Role updatedRole = roleRepository.save(existingRole);

        return Response.<RoleDTO>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("Role updated successfully")
                .data(modelMapper.map(updatedRole, RoleDTO.class))
                .build();
    }


    @Override
    public Response<List<RoleDTO>> getAllRoles() {

        List<Role> roles = roleRepository.findAll();

        List<RoleDTO> rolesDTO =  roles.stream()
                .map(role -> modelMapper.map(role, RoleDTO.class))
                .toList();

        return Response.<List<RoleDTO>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Roles' list retrieved successfully")
                .data(rolesDTO)
                .build();
    }

    @Override
    public Response<?> deleteRole(Long id) {

        if(!roleRepository.existsById(id)){
            throw new NotFoundException("Role does not exists");
        }

        roleRepository.deleteById(id);

        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Role deleted successfully")
                .build();
    }
}
