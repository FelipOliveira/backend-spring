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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Role", description = "Roles management API")
@RestController
@CrossOrigin(origins = "http://localhost:8081")
@RequestMapping("/api")
public class RoleController {
    @Autowired
    private IRoleRepository repository;

    @Operation(summary = "Retrieve all Roles")
	@ApiResponses({
		@ApiResponse(responseCode = "200", content = {
			@Content(schema = @Schema(implementation = Role.class), mediaType = "application/json") }),
		@ApiResponse(responseCode = "204", description = "No roles found", content = {
			@Content(schema = @Schema()) }),
		@ApiResponse(responseCode = "500", content = { @Content(schema = @Schema()) }) })
    @GetMapping("/roles")
    ResponseEntity<List<Role>> getAllRoles(){
        List<Role> roles = new ArrayList<>();
        repository.findAll().forEach(roles::add);

        return new ResponseEntity<>(roles, HttpStatus.OK);
    }
}
