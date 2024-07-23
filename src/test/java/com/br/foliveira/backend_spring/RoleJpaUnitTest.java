package com.br.foliveira.backend_spring;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.br.foliveira.backend_spring.model.Role;
import com.br.foliveira.backend_spring.repository.IRoleRepository;

@DataJpaTest
public class RoleJpaUnitTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private IRoleRepository repository;

    @Test
    public void getRoleById_returnsRoleDataById(){
        List<Role> roles = Stream.of(
            new Role(), 
            new Role()
        ).collect(Collectors.toList());
        roles.forEach(entityManager::persist);

        Role roleData = repository.findById(roles.get(0).getId()).get();
        assertThat(roleData).isEqualTo(roles.get(0));
    }

    @Test
    public void postRole_returnsSavedRoleData(){
        Role role = new Role();
        entityManager.persist(role);

        Role roleData = repository.findById(role.getId()).orElse(new Role());
        assertThat(roleData).isEqualTo(role);
    }
}
