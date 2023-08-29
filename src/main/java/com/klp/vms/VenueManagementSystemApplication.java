package com.klp.vms;

import com.klp.vms.dao.ImageDao;
import com.klp.vms.dao.UserDao;
import com.klp.vms.exception.RuntimeError;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.SQLException;


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
