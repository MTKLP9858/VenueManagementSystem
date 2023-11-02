package com.klp.vms;

import com.klp.vms.controller.StadiumController;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.ParseException;

@SpringBootTest
class VenueManagementSystemApplicationTests {

    @Test
    void contextLoads() throws ParseException {
        String accessToken = "145eae28-b5af-4406-94a9-4f91a00e7eb3";
        String venueUUID = "063fd8c6-097e-49a0-b2ea-93bded6b43d4";

        String s = new StadiumController().queryAllVenue(accessToken, "stadium1");

        System.out.println(s);

    }

}
