package com.wiwitech.mecanetbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class MecanetbackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(MecanetbackendApplication.class, args);
    }

}
