package com.klp.vms;

import com.klp.vms.dao.UserDao;
import com.klp.vms.dao.VenueDao;
import com.klp.vms.entity.Venue;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class VenueManagementSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(VenueManagementSystemApplication.class, args);
        try {
            Venue v = new VenueDao().execQueryBy("a11", "aaa");
            System.out.println(v);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
