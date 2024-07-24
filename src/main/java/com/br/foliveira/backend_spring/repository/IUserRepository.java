package com.br.foliveira.backend_spring.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.br.foliveira.backend_spring.model.User;

@Repository
public interface IUserRepository extends JpaRepository<User, Long> {
    List<User> findByUsername(String username);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
}
