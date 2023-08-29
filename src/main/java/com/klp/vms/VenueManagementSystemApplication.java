package com.klp.vms;

import com.klp.vms.dao.UserDao;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class VenueManagementSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(VenueManagementSystemApplication.class, args);
        try {
            new UserDao().queryAvatar("user1");

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
