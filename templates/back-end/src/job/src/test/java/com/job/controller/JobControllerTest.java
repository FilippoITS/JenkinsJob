package com.job.controller;

import com.job.dto.JobDTO;
import com.job.entity.Job;
import com.job.service.JobService;
import com.job.utilities.Conversion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@ExtendWith(MockitoExtension.class)
public class JobControllerTest {

    @Mock
    private JobService jobService;

    @InjectMocks
    private JobController jobController;

    private MockMvc mockMvc;

    private Job job1;
    private Job job2;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(jobController).build();

        job1 = new Job(1, "Prova1", LocalDateTime.of(2023, 1, 1, 9, 0), LocalDateTime.of(2023, 1, 2, 17, 0), "In Progress", "Data A");
        job2 = new Job(2, "Prova2", LocalDateTime.of(2023, 2, 1, 10, 0), LocalDateTime.of(2023, 2, 2, 18, 0), "Completed", "Data B");
    }

    @Test
    void testGetAllJobs() throws Exception {

        List<Job> jobs = Arrays.asList(job1, job2);

        when(jobService.getAllJobs()).thenReturn(jobs);

        // Esegui la chiamata al controller e verifica la risposta
        mockMvc.perform(get("/job"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("[{\"id\":1,\"project\":\"Prova1\",\"startdate\":\"2023-01-01T09:00:00\",\"enddate\":\"2023-01-02T17:00:00\",\"status\":\"In Progress\",\"data\":\"Data A\"},"
                        + "{\"id\":2,\"project\":\"Prova 2\",\"startdate\":\"2023-02-01T10:00:00\",\"enddate\":\"2023-02-02T18:00:00\",\"status\":\"Completed\",\"data\":\"Data B\"}]"));
    }
}
