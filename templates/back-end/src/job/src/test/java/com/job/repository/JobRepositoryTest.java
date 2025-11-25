package com.job.repository;

import com.job.entity.Job;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class JobRepositoryTest {

    @Autowired
    private JobRepository jobRepository;

    @Test
    void testSaveAndFindById() {
        // 1. Arrange: Creiamo un oggetto Job
        Job job = new Job();
        job.setProject("Test Repository Project");
        job.setStatus("OPEN");
        job.setStartdate(LocalDateTime.now());
        job.setEnddate(LocalDateTime.now().plusDays(10));
        job.setData("Some Data");

        // 2. Act: Salviamo nel DB H2
        Job savedJob = jobRepository.save(job);

        // 3. Assert: Verifichiamo che sia stato salvato
        assertThat(savedJob).isNotNull();
        assertThat(savedJob.getId()).isNotNull(); // L'ID deve essere generato ( > 0)

        // Verifichiamo il recupero tramite ID
        Optional<Job> foundJob = jobRepository.findById(savedJob.getId());
        assertThat(foundJob).isPresent();
        assertThat(foundJob.get().getProject()).isEqualTo("Test Repository Project");
    }

    @Test
    void testFindAll() {
        // 1. Arrange
        Job job1 = new Job();
        job1.setProject("Job 1");

        Job job2 = new Job();
        job2.setProject("Job 2");

        jobRepository.save(job1);
        jobRepository.save(job2);

        // 2. Act
        List<Job> jobs = jobRepository.findAll();

        // 3. Assert
        assertThat(jobs).hasSize(2);
    }

    @Test
    void testDelete() {
        // 1. Arrange
        Job job = new Job();
        job.setProject("To be deleted");
        Job savedJob = jobRepository.save(job);

        // 2. Act
        jobRepository.deleteById(savedJob.getId());

        // 3. Assert
        Optional<Job> deletedJob = jobRepository.findById(savedJob.getId());
        assertThat(deletedJob).isEmpty();
    }
}