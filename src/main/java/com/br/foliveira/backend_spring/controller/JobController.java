package com.br.foliveira.backend_spring.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;

import com.br.foliveira.backend_spring.model.Job;
import com.br.foliveira.backend_spring.model.User;
import com.br.foliveira.backend_spring.repository.IJobRepository;
import com.br.foliveira.backend_spring.repository.IUserRepository;

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
public class JobController {
    @Autowired 
	private IJobRepository jobRepository;

	@Autowired
	private IUserRepository userRepository;

    @GetMapping("/jobs")
    ResponseEntity<List<Job>> getAllJobs(@RequestParam(required = false) String title) {
	    List<Job> jobs = new ArrayList<>();
		if(title == null){
			jobRepository.findAll().forEach(jobs::add);
		}else{
			jobRepository.findByTitleContaining(title).forEach(jobs::add);
		}

		return jobs.isEmpty() ? 
			new ResponseEntity<>(HttpStatus.NO_CONTENT)
			: new ResponseEntity<>(jobs, HttpStatus.OK);
	}

	@GetMapping("/job/{id}")
	public ResponseEntity<Job> getJobById(@PathVariable("id") long id) {
		return jobRepository.findById(id)
			.map(job -> new ResponseEntity<>(job, HttpStatus.OK))
			.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}
	

    @PostMapping("/job")
	public ResponseEntity<Job> postJob(@RequestBody Job jobRequest) {	
		try {
			Job jobData = jobRepository.save(new Job(jobRequest.getTitle(), jobRequest.getDescription()));
			return new ResponseEntity<>(jobData, HttpStatus.CREATED);
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

    @PutMapping("/job/{id}")
	public ResponseEntity<Job> putJob(@PathVariable("id") long id, @RequestBody Job job) {
		return jobRepository.findById(id)
			.map(updatedJob -> {
				updatedJob.setTitle(job.getTitle());
				updatedJob.setDescription(job.getDescription());
				updatedJob.setUsers(job.getUsers());
				return new ResponseEntity<>(jobRepository.save(updatedJob), HttpStatus.OK);
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

    @DeleteMapping("/job/{id}")
	public ResponseEntity<HttpStatus> deleteJobById(@PathVariable("id") long id) {
	    try {
	        jobRepository.deleteById(id);
	        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	    } catch (Exception e) {
	        return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
	    }
	}
}
