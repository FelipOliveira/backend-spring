package com.br.foliveira.backend_spring.controller;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.br.foliveira.backend_spring.model.Job;
import com.br.foliveira.backend_spring.model.User;
import com.br.foliveira.backend_spring.repository.IJobRepository;
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
    private IUserRepository userRepository;

	@Autowired 
	private IJobRepository jobRepository;

    @GetMapping("/user/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") long id) {
        return userRepository.findById(id)
			.map(user -> new ResponseEntity<>(user, HttpStatus.OK))
			.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    @PostMapping("/user")
	public ResponseEntity<User> postUser(@RequestBody User user) {
	    try {
	    	User userData = userRepository
	            .save(
                    new User(user.getUsername(), user.getEmail(), user.getPassword())
                );
	        return new ResponseEntity<>(userData, HttpStatus.CREATED);
	    } catch (Exception e) {
	        return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}

	@PostMapping("/user/{userId}/jobs")
	public ResponseEntity<Job> addJobToUser(@PathVariable(value = "userId")
	Long userId, @RequestBody Job jobRequest) throws Exception {
		Job jobData = userRepository.findById(userId).map(user -> {
			Set<Job> jobs = userRepository.findById(userId).get().getJobs();
			if (!jobs.contains(jobRequest)) {
				user.addJob(jobRequest);
				return jobRepository.save(jobRequest);
			}
			return jobRequest;
		}).orElseThrow(() -> new Exception());

		return new ResponseEntity<>(jobData, HttpStatus.CREATED);
	}

    @PutMapping("/user/{id}")
	public ResponseEntity<User> putUser(@PathVariable("id") long id, @RequestBody User user) {
	    return userRepository.findById(id)
			.map(updatedUser -> {
				updatedUser.setUsername(user.getUsername());
				updatedUser.setEmail(user.getEmail());
				updatedUser.setJobs(user.getJobs());
				return new ResponseEntity<>(userRepository.save(updatedUser), HttpStatus.OK);
			}).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	@DeleteMapping("/user/{userId}/jobs/{jobId}")
	public ResponseEntity<HttpStatus> removeJobFromUser(@PathVariable(value = "userId") Long userId,
	@PathVariable(value = "jobId") Long jobId) throws Exception {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new Exception());
		user.removeJob(jobId);
		userRepository.save(user);
		
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

    @DeleteMapping("/user/{id}")
	public ResponseEntity<Void> deleteUser(@PathVariable("id") long id) {		
		try {
	        userRepository.deleteById(id);
	        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	    } catch (Exception e) {
	        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}
}
