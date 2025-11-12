package com.soa.student;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StudentServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudentServiceApplication.class, args);
        System.out.println("\n✅ Student Service is running on port 8083");
        System.out.println("📄 Swagger UI: http://localhost:8083/docs");
        System.out.println("💚 Health Check: http://localhost:8083/actuator/health\n");
    }
}
