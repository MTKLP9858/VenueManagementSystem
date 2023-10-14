package com.klp.vms;

import com.klp.vms.entity.Order;
import com.klp.vms.entity.Venue;
import com.klp.vms.exception.RuntimeError;
import com.klp.vms.service.OrderService;
import com.klp.vms.service.StadiumService;
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
            long start = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2023-09-28 09:24:39").getTime();
            long end = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2023-09-28 15:08:40").getTime();


            ArrayList<Order> orders = OrderService.queryOrderByTime("62b9762d-dd99-4154-9094-1388d6423a6a", start, end);
            for (Order order : orders) {
                System.out.println(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
