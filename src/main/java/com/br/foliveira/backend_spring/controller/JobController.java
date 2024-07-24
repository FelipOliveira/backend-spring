package com.br.foliveira.backend_spring.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;

import com.br.foliveira.backend_spring.model.Job;
import com.br.foliveira.backend_spring.repository.IJobRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Job", description = "Jobs management API")
@RestController
@CrossOrigin(origins = "http://localhost:8081")
@RequestMapping("/api")
public class JobController {
    @Autowired 
	private IJobRepository repository;

	@Operation(summary = "Retrieve all Jobs")
	@ApiResponses({
		@ApiResponse(responseCode = "200", content = {
			@Content(schema = @Schema(implementation = Job.class), mediaType = "application/json") }),
		@ApiResponse(responseCode = "204", description = "No jobs found", content = {
			@Content(schema = @Schema()) }),
		@ApiResponse(responseCode = "500", content = { @Content(schema = @Schema()) }) })
    @GetMapping("/jobs")
    ResponseEntity<List<Job>> getAllJobs(@RequestParam(required = false) String title) {
	    List<Job> jobs = new ArrayList<>();
		if(title == null){
			repository.findAll().forEach(jobs::add);
		}else{
			repository.findByTitleContaining(title).forEach(jobs::add);
		}

		return jobs.isEmpty() ? 
			new ResponseEntity<>(HttpStatus.NO_CONTENT)
			: new ResponseEntity<>(jobs, HttpStatus.OK);
	}

	@Operation(summary = "Retrieve a Job by Id")
  	@ApiResponses({
		@ApiResponse(responseCode = "200", content = { 
			@Content(schema = @Schema(implementation = Job.class), mediaType = "application/json") }),
		@ApiResponse(responseCode = "404", content = { @Content(schema = @Schema()) }),
		@ApiResponse(responseCode = "500", content = { @Content(schema = @Schema()) }) })
	@GetMapping("/jobs/{id}")
	public ResponseEntity<Job> getJobById(@PathVariable("id") long id) {
		return repository.findById(id)
			.map(job -> new ResponseEntity<>(job, HttpStatus.OK))
			.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}
	
	@Operation(summary = "Create a new Job")
	@ApiResponses({
		@ApiResponse(responseCode = "201", content = {
			@Content(schema = @Schema(implementation = Job.class), mediaType = "application/json") }),
		@ApiResponse(responseCode = "500", content = { @Content(schema = @Schema()) }) })
    @PostMapping("/jobs")
	public ResponseEntity<Job> postJob(@RequestBody Job jobRequest) {	
		try {
			Job jobData = repository.save(new Job(jobRequest.getTitle(), jobRequest.getDescription()));
			return new ResponseEntity<>(jobData, HttpStatus.CREATED);
	    } catch (Exception e) {
	        return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}

	@Operation(summary = "Update a Job by Id")
	@ApiResponses({
		@ApiResponse(responseCode = "200", content = {
			@Content(schema = @Schema(implementation = Job.class), mediaType = "application/json") }),
		@ApiResponse(responseCode = "500", content = { @Content(schema = @Schema()) }),
		@ApiResponse(responseCode = "404", content = { @Content(schema = @Schema()) }) })
    @PutMapping("/jobs/{id}")
	public ResponseEntity<Job> putJob(@PathVariable("id") long id, @RequestBody Job job) {
		return repository.findById(id)
			.map(updatedJob -> {
				updatedJob.setTitle(job.getTitle());
				updatedJob.setDescription(job.getDescription());
				updatedJob.setUsers(job.getUsers());
				return new ResponseEntity<>(repository.save(updatedJob), HttpStatus.OK);
			}).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	@Operation(summary = "Delete a Job by Id")
	@ApiResponses({ @ApiResponse(responseCode = "204", content = { @Content(schema = @Schema()) }),
		@ApiResponse(responseCode = "500", content = { @Content(schema = @Schema()) }) })
    @DeleteMapping("/jobs/{id}")
	public ResponseEntity<HttpStatus> deleteJobById(@PathVariable("id") long id) {
	    try {
	        repository.deleteById(id);
	        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	    } catch (Exception e) {
	        return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
	    }
	}
}
