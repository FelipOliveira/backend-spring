package com.br.foliveira.backend_spring;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.br.foliveira.backend_spring.model.Job;
import com.br.foliveira.backend_spring.repository.IJobRepository;


@DataJpaTest
public class JobJpaUnitTest {
    
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private IJobRepository jobRepository;

    @Test
    public void getAllJobs_returnsEmpty(){
        Iterable jobs = jobRepository.findAll();
        assertThat(jobs).isEmpty();
    }

    @Test
    public void getAllJobs_returnsListWithThreeJobs(){
        List<Job> jobs = Stream.of(
            new Job("title 1","description 1"),
            new Job("title 2","description 2"),
            new Job("title 3","description3" )
        ).collect(Collectors.toList());

        jobs.forEach(entityManager::persist);
        Iterable<Job> jobsData = jobRepository.findAll();
        assertThat(jobsData).hasSize(3).containsAll(jobs);
    }

    @Test
    public void getAllJobs_returnJobsWithTitleContainingString(){
        List<Job> jobs = Stream.of(
            new Job("Java","programming language"),
            new Job("Angular","frontend framework"),
            new Job("Javascript","programming language")
        ).collect(Collectors.toList());

        jobs.forEach(entityManager::persist);
        Iterable<Job> jobsData = jobRepository.findByTitleContaining("Java");
        assertThat(jobsData).hasSize(2).contains(jobs.get(0), jobs.get(2));
    }

    @Test
    public void getJobById_returnJobDataById(){
        List<Job> jobs = Stream.of(
            new Job("title 1","description 1"),
            new Job("title 2","description 2"),
            new Job("title 3","description 3")
        ).collect(Collectors.toList());

        jobs.forEach(entityManager::persist);
        Job jobData = jobRepository.findById(jobs.get(1).getId()).get();
        assertThat(jobData).isEqualTo(jobs.get(1));
    }

    @Test
    public void postJob_returnsSavedJobData(){
        Job job = new Job("Test", "Test description");

        assertThat(job).hasFieldOrPropertyWithValue("title", "Test");
        assertThat(job).hasFieldOrPropertyWithValue("description", "Test description");
    }

    @Test
    public void putJob_returnsUpdatedJobData(){
        List<Job> jobs = Stream.of(
            new Job("title 1","description 1"),
            new Job("title 2","description 2")
        ).collect(Collectors.toList());
        jobs.forEach(entityManager::persist);

        Job updatedJob = new Job("updated job", "updated description");
        Job jobData = jobRepository.findById(jobs.get(1).getId()).get();
        jobData.setTitle(updatedJob.getTitle());
        jobData.setDescription(updatedJob.getDescription());
        jobRepository.save(jobData);

        Job checkJob = jobRepository.findById(jobs.get(1).getId()).get();
        assertThat(checkJob.getId()).isEqualTo(jobs.get(1).getId());
        assertThat(checkJob.getTitle()).isEqualTo(updatedJob.getTitle());
        assertThat(checkJob.getDescription()).isEqualTo(updatedJob.getDescription());
    }

    @Test
    public void deleteJobById_returnsListWithTwoJobs(){
        List<Job> jobs = Stream.of(
            new Job("title 1","description 1"),
            new Job("title 2","description 2"),
            new Job("title 3","description 3")
        ).collect(Collectors.toList());
        
        jobs.forEach(entityManager::persist);
        jobRepository.deleteById(jobs.get(1).getId());

        Iterable<Job> jobData = jobRepository.findAll();
        assertThat(jobData).hasSize(2).contains(jobs.get(0), jobs.get(2));
    }
}
