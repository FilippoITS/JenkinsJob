package com.job.utilities;

import com.job.dto.JobDTO;
import com.job.entity.Job;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ConversionTest {

    @Test
    void testConvertToDTO() {
        // 1. Arrange: Prepariamo l'oggetto Job (Entity) pieno di dati
        Job job = new Job();
        Integer id = 99;
        String project = "Progetto Conversione";
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusDays(7);
        String status = "ACTIVE";
        String data = "{\"meta\": \"data\"}";

        job.setId(id);
        job.setProject(project);
        job.setStartdate(start);
        job.setEnddate(end);
        job.setStatus(status);
        job.setData(data);

        // 2. Act: Chiamiamo il metodo statico di conversione
        JobDTO resultDTO = Conversion.convertToDTO(job);

        // 3. Assert: Verifichiamo che ogni singolo campo sia stato copiato correttamente
        assertNotNull(resultDTO, "Il DTO restituito non deve essere null");
        assertEquals(id, resultDTO.getId());
        assertEquals(project, resultDTO.getProject());
        assertEquals(start, resultDTO.getStartdate());
        assertEquals(end, resultDTO.getEnddate());
        assertEquals(status, resultDTO.getStatus());
        assertEquals(data, resultDTO.getData());
    }

    @Test
    void testConvertToDTO_WithNullFields() {
        // Test per verificare che non esploda se i campi interni sono null
        Job job = new Job();
        job.setId(1);
        // Lasciamo gli altri campi null apposta

        JobDTO resultDTO = Conversion.convertToDTO(job);

        assertNotNull(resultDTO);
        assertEquals(1, resultDTO.getId());
        assertNull(resultDTO.getProject());
        assertNull(resultDTO.getStartdate());
    }

    /**
     * Test "Inutile" ma necessario per il 100% coverage su SonarQube.
     * Poiché la classe Conversion non è 'final' e ha un costruttore di default implicito,
     * SonarQube segna la riga "public class Conversion" come non coperta se non la istanziamo.
     */
    @Test
    void testConstructor() {
        Conversion conversion = new Conversion();
        assertNotNull(conversion);
    }
}