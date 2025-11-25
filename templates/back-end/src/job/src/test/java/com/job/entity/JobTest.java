package com.job.entity;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class JobTest {

    @Test
    void testJobGettersAndSetters() {
        // Arrange
        Job job = new Job();

        Integer id = 1;
        String project = "Test Project";
        LocalDateTime now = LocalDateTime.now();
        String status = "OPEN";
        String data = "Some Data";

        // Act
        job.setId(id);
        job.setProject(project);
        job.setStartdate(now);
        job.setEnddate(now);
        job.setStatus(status);
        job.setData(data);

        // Assert
        assertEquals(id, job.getId());
        assertEquals(project, job.getProject());
        assertEquals(now, job.getStartdate());
        assertEquals(now, job.getEnddate());
        assertEquals(status, job.getStatus());
        assertEquals(data, job.getData());
    }

}