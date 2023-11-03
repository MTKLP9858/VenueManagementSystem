package com.klp.vms;

import com.alibaba.fastjson.JSONArray;
import com.klp.vms.entity.Venue;
import com.klp.vms.exception.RuntimeError;
import com.klp.vms.service.StadiumService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

@Slf4j
@SpringBootTest
class VenueManagementSystemApplicationTests {

    @Test
    void contextLoads() throws ParseException, SQLException, RuntimeError {
        String accessToken = "145eae28-b5af-4406-94a9-4f91a00e7eb3";
        String venueUUID = "063fd8c6-097e-49a0-b2ea-93bded6b43d4";

        List<Venue> venues = StadiumService.queryAllVenue(accessToken, "stadium1");
        JSONArray jsonArray = (JSONArray) JSONArray.parse(venues.toString());
        log.info(jsonArray.toString());
    }
}
