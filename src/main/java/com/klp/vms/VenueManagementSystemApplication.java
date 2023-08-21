package com.klp.vms;

import com.alibaba.fastjson.JSONObject;
import com.klp.vms.dao.UserDao;
import com.klp.vms.entity.User;
import com.klp.vms.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.SQLException;


@SpringBootApplication
public class VenueManagementSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(VenueManagementSystemApplication.class, args);
        try {


        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }

    }
}
