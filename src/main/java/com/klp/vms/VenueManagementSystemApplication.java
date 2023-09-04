package com.klp.vms;

import com.klp.vms.dao.ImageDao;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class VenueManagementSystemApplication {
    public static void main(String[] args) {
        try {
            SpringApplication.run(VenueManagementSystemApplication.class, args);
            new ImageDao();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
