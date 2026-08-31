package com.library.lms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * This is the entry point of the whole application.
 * When you run this class, Spring Boot starts an embedded server (Tomcat)
 * and wires together every @Component, @Service, @Repository and @Controller
 * it finds in this package and its sub-packages.
 */
@SpringBootApplication
public class LmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(LmsApplication.class, args);
    }
}
