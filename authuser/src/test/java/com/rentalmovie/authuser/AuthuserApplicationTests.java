package com.rentalmovie.authuser;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class AuthuserApplicationTests {

    @Test
    void contextLoads() {
        // Testa se o contexto carrega corretamente no profile de testes
    }

}
