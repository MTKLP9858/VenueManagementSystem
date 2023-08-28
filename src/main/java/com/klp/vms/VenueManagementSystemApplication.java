package com.klp.vms;

import com.klp.vms.dao.UserDao;
import com.klp.vms.exception.RuntimeError;
import com.klp.vms.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.SQLException;


@SpringBootApplication
public class VenueManagementSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(VenueManagementSystemApplication.class, args);
        try {
            UserService.updateAccessToken("user1");

            System.out.println(new UserDao().execQuery("user1"));
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        } catch (SQLException | RuntimeError e) {
            System.err.println(e.getMessage());
        }

    }
}
