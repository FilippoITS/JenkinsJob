package com.job.entity;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class JobTest {

    @Test
    void testJobEntityGettersAndSetters() {
        // Arrange: Creazione dell'istanza e dei dati di prova
        Job job = new Job();

        Integer id = 1;
        String project = "Progetto Alpha";
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now().plusDays(5);
        String status = "IN_PROGRESS";
        String data = "{\"key\": \"value\"}";

        // Act: Utilizzo dei Setters
        job.setId(id);
        job.setProject(project);
        job.setStartdate(startDate);
        job.setEnddate(endDate);
        job.setStatus(status);
        job.setData(data);

        // Assert: Verifica tramite Getters
        assertEquals(id, job.getId());
        assertEquals(project, job.getProject());
        assertEquals(startDate, job.getStartdate());
        assertEquals(endDate, job.getEnddate());
        assertEquals(status, job.getStatus());
        assertEquals(data, job.getData());
    }

    @Test
    void testJobToStringOrEquals()

        Job job1 = new Job();
        assertNotNull(job1);
}