package com.job.service;

import com.job.entity.Job;
import com.job.repository.JobRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;


import java.util.Arrays;
import java.util.List;
import java.time.LocalDateTime;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JobServiceTest {

    @Mock
    private JobRepository jobRepository;

    @InjectMocks
    private JobService jobService;

    private Job job1;
    private Job job2;

    @BeforeEach
    void setUp() {
        job1 = new Job(1, "prova1", LocalDateTime.of(2023, 1, 1, 9, 0), LocalDateTime.of(2023, 2, 1, 9, 0), "completed", ".");
        job2 = new Job(2, "prova2", LocalDateTime.of(2023, 2, 1, 9, 0), LocalDateTime.of(2023, 3, 1, 9, 0), "completed", ".");
    }

    @Test
    void testGetAllJobs() {

        when(jobRepository.findAll()).thenReturn(Arrays.asList(job1, job2));


        List<Job> jobs = jobService.getAllJobs();


        assertEquals(2, jobs.size(), "Dovrebbero esserci 2 oggetti");
        assertEquals("prova1", jobs.get(0).getProject(), "Il nome del primo job dovrebbe essere 'prova1'");
    }
}
