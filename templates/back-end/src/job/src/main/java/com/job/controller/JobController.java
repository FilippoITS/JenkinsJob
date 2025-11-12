package com.job.controller;

import com.job.dto.JobDTO;
import com.job.entity.Job;
import com.job.service.JobService;
import com.job.utilities.Conversion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping()
public class JobController {

    @Autowired
    private JobService jobService;


    @GetMapping("/job")
    public List<JobDTO> getAllJobs() {
        List<Job> jobs = jobService.getAllJobs();
        List<JobDTO> jobsDTO = new ArrayList<>();
        for (Job j:jobs){
            jobsDTO.add(Conversion.convertToDTO(j));
        }
        return jobsDTO;
    }
}
