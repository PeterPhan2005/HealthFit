package com.soa.auth.config;

import com.soa.auth.entity.Customer;
import com.soa.auth.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitialization implements CommandLineRunner {

    @Autowired
    private CustomerService customerService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedCustomers();
    }

    private void seedCustomers() {
        if (customerService.findByUsername("admin").isEmpty()) {
            Customer c = new Customer("admin", "Peter Phan", passwordEncoder.encode("admin123"),
                    "0900000001", "khantrongclone@gmail.com", 10_000_000);
            customerService.save(c);
            System.out.println("✅ Created customer: admin");
        }

        if (customerService.findByUsername("mary").isEmpty()) {
            Customer c = new Customer("mary", "Mary Nguyen", passwordEncoder.encode("mary123"),
                    "0900000002", "angiatoru09@gmail.com", 15_000_000);
            customerService.save(c);
            System.out.println("✅ Created customer: mary");
        }

        if (customerService.findByUsername("john").isEmpty()) {
            Customer c = new Customer("john", "John Tran", passwordEncoder.encode("john123"),
                    "0900000003", "peter.contact.work@gmail.com", 20_000_000);
            customerService.save(c);
            System.out.println("✅ Created customer: john");
        }
        
        System.out.println("✅ Auth Service: Customer data seeded successfully");
    }
}
