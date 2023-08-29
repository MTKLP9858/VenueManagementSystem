package com.klp.vms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class VenueManagementSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(VenueManagementSystemApplication.class, args);
        try {


        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }
}
