package com.job.controller;

import com.job.entity.Job;
import com.job.service.JobService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class JobControllerTest {

    @Mock
    private JobService jobService;  // Mock del servizio (non repository)

    @InjectMocks
    private JobController jobController;  // Controller da testare

    private Job job1;
    private Job job2;

    @BeforeEach
    void setUp() {
        // Inizializza i job per i test
        job1 = new Job(1, "Prova1", LocalDateTime.of(2023, 1, 1, 9, 0), LocalDateTime.of(2023, 1, 2, 17, 0), "In Progress", "Data A");
        job2 = new Job(2, "Prova2", LocalDateTime.of(2023, 2, 1, 10, 0), LocalDateTime.of(2023, 2, 2, 18, 0), "Completed", "Data B");
    }

    @Test
    void testGetAllJobs() {
        // Simula il comportamento del servizio
        when(jobService.getAllJobs()).thenReturn(Arrays.asList(job1, job2));

        // Chiama il metodo nel servizio
        List<Job> jobs = jobService.getAllJobs();

        // Verifica che il numero di oggetti restituiti sia corretto
        assertEquals(2, jobs.size(), "Dovrebbero esserci 2 oggetti");

        // Verifica che il primo job abbia il nome corretto
        assertEquals("Prova1", jobs.get(0).getProject(), "Il nome del primo job dovrebbe essere 'Prova1'");

        // Verifica che le date siano nel formato corretto (in questo caso, li confrontiamo come LocalDateTime)
        assertEquals(LocalDateTime.of(2023, 1, 1, 9, 0), jobs.get(0).getStartdate(), "La startDate del primo job non è corretta");
        assertEquals(LocalDateTime.of(2023, 1, 2, 17, 0), jobs.get(0).getEnddate(), "La endDate del primo job non è corretta");

        // Aggiungi anche una verifica per il secondo job
        assertEquals("Prova2", jobs.get(1).getProject(), "Il nome del secondo job dovrebbe essere 'Prova2'");
        assertEquals(LocalDateTime.of(2023, 2, 1, 10, 0), jobs.get(1).getStartdate(), "La startDate del secondo job non è corretta");
        assertEquals(LocalDateTime.of(2023, 2, 2, 18, 0), jobs.get(1).getEnddate(), "La endDate del secondo job non è corretta");
    }
}
