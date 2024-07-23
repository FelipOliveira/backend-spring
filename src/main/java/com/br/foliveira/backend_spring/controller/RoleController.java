package com.br.foliveira.backend_spring.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.br.foliveira.backend_spring.model.Role;
import com.br.foliveira.backend_spring.repository.IRoleRepository;

@RestController
@CrossOrigin(origins = "http://localhost:8081")
@RequestMapping("/api")
public class RoleController {
    @Autowired
    private IRoleRepository repository;

    @GetMapping("/roles")
    ResponseEntity<List<Role>> getAllRoles(){
        List<Role> roles = new ArrayList<>();
        repository.findAll().forEach(roles::add);

        return new ResponseEntity<>(roles, HttpStatus.OK);
    }
}
