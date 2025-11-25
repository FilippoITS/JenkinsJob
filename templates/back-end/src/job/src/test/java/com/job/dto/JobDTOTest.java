package com.job.dto;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class JobDTOTest {

    @Test
    void testJobDTOGettersAndSetters() {
        // 1. Arrange: Creiamo l'oggetto e i dati di test
        JobDTO jobDTO = new JobDTO();

        Integer id = 500;
        String project = "Progetto DTO";
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now().plusMonths(1);
        String status = "PLANNED";
        String data = "Info aggiuntive";

        // 2. Act: Impostiamo i valori usando i Setter
        jobDTO.setId(id);
        jobDTO.setProject(project);
        jobDTO.setStartdate(startDate);
        jobDTO.setEnddate(endDate);
        jobDTO.setStatus(status);
        jobDTO.setData(data);

        // 3. Assert: Verifichiamo che i Getter restituiscano i valori corretti
        assertEquals(id, jobDTO.getId());
        assertEquals(project, jobDTO.getProject());
        assertEquals(startDate, jobDTO.getStartdate());
        assertEquals(endDate, jobDTO.getEnddate());
        assertEquals(status, jobDTO.getStatus());
        assertEquals(data, jobDTO.getData());
    }
}