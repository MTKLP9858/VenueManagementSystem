package com.klp.vms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class VenueManagementSystemApplication {
    public static void main(String[] args) throws ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        try {
            SpringApplication.run(VenueManagementSystemApplication.class, args);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
