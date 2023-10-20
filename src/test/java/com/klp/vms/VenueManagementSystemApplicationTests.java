package com.klp.vms;

import com.klp.vms.dao.VenueDao;
import com.klp.vms.entity.Order;
import com.klp.vms.entity.Venue;
import com.klp.vms.exception.RuntimeError;
import com.klp.vms.service.OrderService;
import com.klp.vms.service.StadiumService;
import com.klp.vms.service.VenueService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

@SpringBootTest
class VenueManagementSystemApplicationTests {

    @Test
    void contextLoads() throws ParseException {
        try {
            String accessToken = "145eae28-b5af-4406-94a9-4f91a00e7eb3";
            String venueUUID = "063fd8c6-097e-49a0-b2ea-93bded6b43d4";
            // OrderService.newOrder(accessToken, "user1", venueUUID, new Date().getTime() + 10 * 60 * 1000, new Date().getTime() + 20 * 60 * 1000, "infor111111111", "message2222222222");
            System.out.println(OrderService.queryOrderByVenueUUID(venueUUID));
            new VenueDao().execUpdate("price", 20, "063fd8c6-097e-49a0-b2ea-93bded6b43d4");
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (RuntimeError e) {
            throw new RuntimeException(e);
        }
    }

}
