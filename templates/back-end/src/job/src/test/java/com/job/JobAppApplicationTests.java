package com.job;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@ActiveProfiles("test")
class JobAppApplicationTests {

	@Test
	void contextLoads() {
        // Questo test verifica solo che il contesto Spring Boot si avvii correttamente.
        // Nessuna implementazione necessaria.
	}

}
