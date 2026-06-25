package com.scih;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Ponto de entrada da aplicação SCIH Backend.
 *
 * Para rodar em desenvolvimento (mock service):
 *   ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
 *
 * Para rodar em produção:
 *   SPRING_PROFILES_ACTIVE=prod java -jar scih-backend.jar
 */
@SpringBootApplication
@EnableScheduling
public class ScihApplication {

    public static void main(String[] args) {
        SpringApplication.run(ScihApplication.class, args);
    }
}
