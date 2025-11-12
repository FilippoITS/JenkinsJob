package com.job.service;

import com.job.entity.Job;
import com.job.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobService {

    @Autowired
    private JobRepository jobRepository;

    // Ottenere tutti i Job
    public List<Job> getAllJobs() {
        return jobRepository.findAll();
    }
}