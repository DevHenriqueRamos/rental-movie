package com.rentalmovie.movie;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class MovieApplicationTests {

    @Test
    void contextLoads() {
        // Testa se o contexto carrega corretamente no profile de testes
    }

}
