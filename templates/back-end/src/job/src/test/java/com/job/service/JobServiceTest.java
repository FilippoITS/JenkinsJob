package com.job.service;

import com.job.entity.Job;
import com.job.repository.JobRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

// @ExtendWith(MockitoExtension.class) abilita l'uso di @Mock e @InjectMocks
// È molto più leggero e veloce di @SpringBootTest
@ExtendWith(MockitoExtension.class)
class JobServiceTest {

    // @Mock crea una versione "finta" del repository.
    // Non si connette al database.
    @Mock
    private JobRepository jobRepository;

    // @InjectMocks crea un'istanza reale di JobService e ci inietta dentro
    // il mock del jobRepository creato sopra.
    @InjectMocks
    private JobService jobService;

    @Test
    void testGetAllJobs() {
        // 1. ARRANGE: Prepariamo i dati che il mock deve restituire
        Job job1 = new Job();
        job1.setId(1);
        job1.setProject("Service Test Project 1");

        Job job2 = new Job();
        job2.setId(2);
        job2.setProject("Service Test Project 2");

        List<Job> expectedJobs = Arrays.asList(job1, job2);

        // Istruiamo il Mockito: "Quando qualcuno chiama jobRepository.findAll(), restituisci expectedJobs"
        when(jobRepository.findAll()).thenReturn(expectedJobs);

        // 2. ACT: Chiamiamo il metodo del servizio
        List<Job> actualJobs = jobService.getAllJobs();

        // 3. ASSERT: Verifiche
        assertEquals(2, actualJobs.size(), "Dovrebbe restituire 2 job");
        assertEquals("Service Test Project 1", actualJobs.get(0).getProject());

        // Verifica fondamentale: controlliamo che il servizio abbia effettivamente chiamato il repository
        verify(jobRepository, times(1)).findAll();
    }
}