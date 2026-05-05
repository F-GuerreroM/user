package com.usuario.user;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "spring.datasource.url=jdbc:oracle:thin:@localhost:1521/xe")
class UserApplicationTests {

    @Test
    void contextLoads() {
    }

}