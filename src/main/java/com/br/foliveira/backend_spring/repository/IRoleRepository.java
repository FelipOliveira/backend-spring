package com.br.foliveira.backend_spring.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.br.foliveira.backend_spring.model.ERole;
import com.br.foliveira.backend_spring.model.Role;

public interface IRoleRepository  extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);
}
