package com.job.utilities;

import com.job.dto.JobDTO;
import com.job.entity.Job;

public class Conversion {
    public static JobDTO convertToDTO(Job job) {
        JobDTO jobDTO = new JobDTO();
        jobDTO.setId(job.getId());
        jobDTO.setProject(job.getProject());
        jobDTO.setStartdate(job.getStartdate());
        jobDTO.setEnddate(job.getEnddate());
        jobDTO.setStatus(job.getStatus());
        jobDTO.setData(job.getData());
        return jobDTO;
    }
}
