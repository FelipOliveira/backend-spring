package com.br.foliveira.backend_spring.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.br.foliveira.backend_spring.model.User;
import com.br.foliveira.backend_spring.repository.IUserRepository;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@CrossOrigin(origins = "http://localhost:8081")
@RequestMapping("/api")
public class UserController {
    @Autowired
    private IUserRepository repository;

    @GetMapping("/usuario/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") long id) {
        return repository.findById(id)
			.map(user -> new ResponseEntity<>(user, HttpStatus.OK))
			.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    @PostMapping("/usuario")
	public ResponseEntity<User> postUser(@RequestBody User user) {
	    try {
	    	User userData = repository
	            .save(
                    new User(user.getUsername(), user.getEmail(), user.getPassword())
                );
	        return new ResponseEntity<>(userData, HttpStatus.CREATED);
	    } catch (Exception e) {
	        return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}

    @PutMapping("/usuario/{id}")
	public ResponseEntity<User> putUser(@PathVariable("id") long id, @RequestBody User user) {
	    return repository.findById(id)
			.map(updatedUser -> {
				updatedUser.setUsername(user.getUsername());
				updatedUser.setEmail(user.getEmail());
				updatedUser.setJobs(user.getJobs());
				return new ResponseEntity<>(repository.save(updatedUser), HttpStatus.OK);
			}).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

    @DeleteMapping("/usuario/{id}")
	public ResponseEntity<Void> deleteUser(@PathVariable("id") long id) {		
		try {
	        repository.deleteById(id);
	        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	    } catch (Exception e) {
	        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}
}
