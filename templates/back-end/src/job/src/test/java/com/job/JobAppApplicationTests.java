package com.job;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class JobAppApplicationTests {

    @Autowired
    private ApplicationContext context;

    @Test
    void contextLoads() {
        // Verifica che il contesto di Spring sia stato caricato correttamente.
        // Se l'app non parte, questo test fallisce.
        assertThat(context).isNotNull();
    }

    /**
     * Questo test serve specificamente per SonarQube.
     * La classe principale JobAppApplication ha un metodo 'main' statico.
     * Senza questo test, SonarQube segnerà quel file come "non coperto".
     */
    @Test
    void mainMethodTest() {
        // Chiama il metodo main per garantire la coverage
        JobAppApplication.main(new String[] {});
    }
}