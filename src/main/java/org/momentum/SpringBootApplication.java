package org.momentum;


import org.springframework.boot.SpringApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@org.springframework.boot.autoconfigure.SpringBootApplication
@EnableJpaAuditing
public class SpringBootApplication {
        public static void main(String[] args) {
            SpringApplication.run(SpringBootApplication.class, args);
        }

}