package com.br.foliveira.backend_spring;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.br.foliveira.backend_spring.model.User;
import com.br.foliveira.backend_spring.repository.IUserRepository;

@DataJpaTest
public class UserJpaUnitTest {
    
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private IUserRepository repository;

    @Test
    public void getUserById_returnUserDataById(){
        List<User> users = Stream.of(
            new User("name 1","email1@email.com", "123456"),
            new User("name 2","email2@email.com", "123456"),
            new User("name 3","email3@email.com", "123456")
        ).collect(Collectors.toList());

        users.forEach(entityManager::persist);
        User userData = repository.findById(users.get(1).getId()).get();
        assertThat(userData).isEqualTo(users.get(1));
    }

    @Test
    public void postUser_returnsSavedUserData(){
        User user = new User("name1", "email@test.com", "123456");
        entityManager.persist(user);

        User userData = repository.findById(user.getId()).orElse(new User());

        assertThat(userData).hasFieldOrPropertyWithValue("username", "name1");
        assertThat(userData).hasFieldOrPropertyWithValue("email", "email@test.com");
        assertThat(userData).hasFieldOrPropertyWithValue("password", "123456");
    }

    @Test
    public void putUser_returnsUpdatedUserData(){
        List<User> users = Stream.of(
            new User("name 1","email1@email.com", "123456"),
            new User("name 2","email2@email.com", "123456")
        ).collect(Collectors.toList());
        users.forEach(entityManager::persist);

        User updatedUser = new User("updated name", "updated@email.com", "newpassword");
        User userData = repository.findById(users.get(1).getId()).get();
        userData.setUsername(updatedUser.getUsername());
        userData.setEmail(updatedUser.getEmail());
        userData.setPassword(updatedUser.getPassword());
        repository.save(userData);

        User checkUser = repository.findById(users.get(1).getId()).get();
        assertThat(checkUser.getId()).isEqualTo(users.get(1).getId());
        assertThat(checkUser.getUsername()).isEqualTo(updatedUser.getUsername());
        assertThat(checkUser.getEmail()).isEqualTo(updatedUser.getEmail());
        assertThat(checkUser.getPassword()).isEqualTo(updatedUser.getPassword());
    }

    @Test
    public void deleteUserById_returnsListWithTwoUsers(){
        List<User> users = Stream.of(
            new User("name 1","email1@test.com", "123456"),
            new User("name 2","email2@test.com", "123456"),
            new User("name 3","email3@test.com", "123456")
        ).collect(Collectors.toList());
        
        users.forEach(entityManager::persist);
        repository.deleteById(users.get(1).getId());

        Iterable<User> userData = repository.findAll();
        assertThat(userData).hasSize(2).contains(users.get(0), users.get(2));
    }
}
