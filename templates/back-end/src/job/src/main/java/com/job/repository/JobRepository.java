package com.job.repository;

import com.job.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRepository extends JpaRepository<Job, Integer> {
    // Puoi aggiungere query personalizzate qui, se necessario
}
