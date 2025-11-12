package com.soa.customer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CustomerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustomerServiceApplication.class, args);
        System.out.println("\n✅ Customer Service is running on port 8082");
        System.out.println("📄 Swagger UI: http://localhost:8082/docs");
        System.out.println("💚 Health Check: http://localhost:8082/actuator/health\n");
    }
}
