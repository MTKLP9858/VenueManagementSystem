package com.klp.vms;

import com.klp.vms.entity.Venue;
import com.klp.vms.exception.RuntimeError;
import com.klp.vms.service.OrderService;
import com.klp.vms.service.StadiumService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.SQLException;
import java.util.Date;

@SpringBootTest
class VenueManagementSystemApplicationTests {

    @Test
    void contextLoads() {
        try {
            String accessToken = "145eae28-b5af-4406-94a9-4f91a00e7eb3";
            Date startTime = new Date(new Date().getTime() + 2 * 24 * 60 * 60 * 1000);
            Date endTime = new Date(new Date().getTime() + 3 * 24 * 60 * 60 * 1000);
            OrderService.newOrder(accessToken, "user2", "62b9762d-dd99-4154-9094-1388d6423a6a", startTime.getTime(), endTime.getTime(), null, null);
        } catch (SQLException | RuntimeError e) {
            e.printStackTrace();
        }
    }

}
