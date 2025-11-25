package com.job.controller;

import com.job.entity.Job;
import com.job.service.JobService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(JobController.class)
class JobControllerTest {

    @Autowired
    private MockMvc mockMvc; // Simula le chiamate HTTP

    @MockBean
    private JobService jobService; // Simula il Service (nessun accesso al DB reale)

    @Test
    void testGetAllJobs_ReturnsListOfDTOs() throws Exception {
        // 1. ARRANGE: Prepariamo i dati finti che il Service dovrebbe restituire
        Job job1 = new Job();
        job1.setId(1);
        job1.setProject("Project Alpha");
        job1.setStatus("OPEN");
        job1.setStartdate(LocalDateTime.now());
        // ... altri campi se necessario per Conversion

        Job job2 = new Job();
        job2.setId(2);
        job2.setProject("Project Beta");
        job2.setStatus("CLOSED");
        job2.setStartdate(LocalDateTime.now());

        List<Job> mockJobs = Arrays.asList(job1, job2);

        // Diciamo a Mockito: "Quando qualcuno chiama jobService.getAllJobs(), restituisci mockJobs"
        when(jobService.getAllJobs()).thenReturn(mockJobs);

        // 2. ACT & ASSERT: Eseguiamo la chiamata HTTP GET e verifichiamo il JSON di risposta
        mockMvc.perform(get("/job")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Verifica HTTP 200
                .andExpect(jsonPath("$", hasSize(2))) // Verifica che la lista abbia 2 elementi
                .andExpect(jsonPath("$[0].project", is("Project Alpha"))) // Verifica i dati convertiti
                .andExpect(jsonPath("$[1].project", is("Project Beta")));
    }

    @Test
    void testGetAllJobs_ReturnsEmptyList() throws Exception {
        // Test caso lista vuota
        when(jobService.getAllJobs()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/job")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}