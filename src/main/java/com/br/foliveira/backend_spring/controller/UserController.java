package com.br.foliveira.backend_spring.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@CrossOrigin(origins = "http://localhost:8081")
@RequestMapping("/api")
public class UserController {
    @Autowired
    private IUserRepository userRepository;

	@Autowired 
	private IJobRepository jobRepository;

	@GetMapping("/users")
    ResponseEntity<List<User>> getAllUSers(@RequestParam(required = false) String username) {
		List<User> users = new ArrayList<>();
		if(username == null){
			userRepository.findAll().forEach(users::add);
		}else{
			userRepository.findByUsername(username).forEach(users::add);
		}
		
		return users.isEmpty() ? 
		new ResponseEntity<>(HttpStatus.NO_CONTENT)
		: new ResponseEntity<>(users, HttpStatus.OK);
	}
	
    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") long id) {
		return userRepository.findById(id)
		.map(user -> new ResponseEntity<>(user, HttpStatus.OK))
		.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
	
	@GetMapping("users/{userId}/jobs")
	public ResponseEntity<List<Job>> getJobsAvaliable
	(@PathVariable(value = "userId") long userId) {
		Set<Job> userJobData = userRepository.findById(userId).get().getJobs();
		List<Job> jobsAvaliable = jobRepository.findAll().stream()
			.filter(job -> !userJobData.contains(job)).collect(Collectors.toList());

		return new ResponseEntity<>(jobsAvaliable, HttpStatus.OK);
	}
    
    @PostMapping("/users")
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

	@PostMapping("/users/{userId}/jobs")
	public ResponseEntity<User> addJobToUser(@PathVariable(value = "userId")
	Long userId, @RequestBody Job jobRequest) throws Exception {
		User userData = userRepository.findById(userId).get();
		userRepository.findById(userId).map(user -> {
			Set<Job> jobs = userData.getJobs();
			if (!jobs.contains(jobRequest)) {
				user.addJob(jobRequest);
				jobRepository.save(jobRequest);
			}
			return userData;
		}).orElseThrow(() -> new Exception());

		return new ResponseEntity<>(userData, HttpStatus.CREATED);
	}

    @PutMapping("/users/{id}")
	public ResponseEntity<User> putUser(@PathVariable("id") long id, @RequestBody User user) {
	    return userRepository.findById(id)
			.map(updatedUser -> {
				updatedUser.setUsername(user.getUsername());
				updatedUser.setEmail(user.getEmail());
				updatedUser.setJobs(user.getJobs());
				return new ResponseEntity<>(userRepository.save(updatedUser), HttpStatus.OK);
			}).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	@DeleteMapping("/users/{userId}/jobs/{jobId}")
	public ResponseEntity<HttpStatus> removeJobFromUser(@PathVariable(value = "userId") Long userId,
	@PathVariable(value = "jobId") Long jobId) throws Exception {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new Exception());
		user.removeJob(jobId);
		userRepository.save(user);
		
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

    @DeleteMapping("/users/{id}")
	public ResponseEntity<Void> deleteUser(@PathVariable("id") long id) {		
		try {
	        userRepository.deleteById(id);
	        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	    } catch (Exception e) {
	        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}
}
