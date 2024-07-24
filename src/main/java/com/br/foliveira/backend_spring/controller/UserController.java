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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "User", description = "Users management API")
@RestController
@CrossOrigin(origins = "http://localhost:8081")
@RequestMapping("/api")
public class UserController {
    @Autowired
    private IUserRepository userRepository;

	@Autowired 
	private IJobRepository jobRepository;

	@Operation(summary = "Retrieve all Users")
	@ApiResponses({
		@ApiResponse(responseCode = "200", content = {
			@Content(schema = @Schema(implementation = User.class), mediaType = "application/json") }),
		@ApiResponse(responseCode = "204", description = "No users found", content = {
			@Content(schema = @Schema()) }),
		@ApiResponse(responseCode = "500", content = { @Content(schema = @Schema()) }) })
	@GetMapping("/users")
    ResponseEntity<List<User>> getAllUSers(@RequestParam(required = false) String username) {
		List<User> users = new ArrayList<>();
		if(username == null){
			userRepository.findAll().forEach(users::add);
		}else{
			userRepository.findByUsernameContaining(username).forEach(users::add);
		}
		
		return users.isEmpty() ? 
		new ResponseEntity<>(HttpStatus.NO_CONTENT)
		: new ResponseEntity<>(users, HttpStatus.OK);
	}
	
	@Operation(
		summary = "Retrieve a User by Id")
  	@ApiResponses({
		@ApiResponse(responseCode = "200", content = { 
			@Content(schema = @Schema(implementation = User.class), mediaType = "application/json") }),
		@ApiResponse(responseCode = "404", content = { @Content(schema = @Schema()) }),
		@ApiResponse(responseCode = "500", content = { @Content(schema = @Schema()) }) })
    @GetMapping("/users/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable("userId") long id) {
		return userRepository.findById(id)
		.map(user -> new ResponseEntity<>(user, HttpStatus.OK))
		.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
	
	@Operation(summary = "Retrieve all Jobs avaliable for user")
	@ApiResponses({
		@ApiResponse(responseCode = "200", content = {
			@Content(schema = @Schema(implementation = Job.class), mediaType = "application/json") }),
		@ApiResponse(responseCode = "500", content = { @Content(schema = @Schema()) }) })
	@GetMapping("users/{userId}/jobs")
	public ResponseEntity<List<Job>> getJobsAvaliable
	(@PathVariable(value = "userId") long userId) {
		Set<Job> userJobData = userRepository.findById(userId).get().getJobs();
		List<Job> jobsAvaliable = jobRepository.findAll().stream()
			.filter(job -> !userJobData.contains(job)).collect(Collectors.toList());

		return new ResponseEntity<>(jobsAvaliable, HttpStatus.OK);
	}
    
	@Operation(summary = "Create a new User")
	@ApiResponses({
		@ApiResponse(responseCode = "201", content = {
			@Content(schema = @Schema(implementation = User.class), mediaType = "application/json") }),
		@ApiResponse(responseCode = "500", content = { @Content(schema = @Schema()) }) })
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

	@Operation(summary = "Add a Job to an existing User")
	@ApiResponses({
		@ApiResponse(responseCode = "201", content = {
			@Content(schema = @Schema(implementation = User.class), mediaType = "application/json") }),
		@ApiResponse(responseCode = "500", content = { @Content(schema = @Schema()) }) })
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

	@Operation(summary = "Update a User by Id")
	@ApiResponses({
		@ApiResponse(responseCode = "200", content = {
			@Content(schema = @Schema(implementation = User.class), mediaType = "application/json") }),
		@ApiResponse(responseCode = "500", content = { @Content(schema = @Schema()) }),
		@ApiResponse(responseCode = "404", content = { @Content(schema = @Schema()) }) })
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

	@Operation(summary = "Remove a existing Job from a User")
	@ApiResponses({ @ApiResponse(responseCode = "204", content = { @Content(schema = @Schema()) }),
		@ApiResponse(responseCode = "500", content = { @Content(schema = @Schema()) }) })
	@DeleteMapping("/users/{userId}/jobs/{jobId}")
	public ResponseEntity<HttpStatus> removeJobFromUser(@PathVariable(value = "userId") Long userId,
	@PathVariable(value = "jobId") Long jobId) throws Exception {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new Exception());
		user.removeJob(jobId);
		userRepository.save(user);
		
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@Operation(summary = "Delete a User by Id")
	@ApiResponses({ @ApiResponse(responseCode = "204", content = { @Content(schema = @Schema()) }),
		@ApiResponse(responseCode = "500", content = { @Content(schema = @Schema()) }) })
    @DeleteMapping("/users/{userId}")
	public ResponseEntity<Void> deleteUser(@PathVariable("userId") long id) {		
		try {
	        userRepository.deleteById(id);
	        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	    } catch (Exception e) {
	        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}
}
