package com.fource.hrbank;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
public class Sb3HrbankFourceApplication {

    @Value("${spring.profiles.active}")
    private String activeProfile;

    public static void main(String[] args) {
        SpringApplication.run(Sb3HrbankFourceApplication.class, args);
    }

    @PostConstruct
    public void printProfile() {
        System.out.println("현재 활성화된 프로파일: " + activeProfile);
    }
}
