package com.klp.vms;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.klp.vms.entity.Venue;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class VenueManagementSystemApplicationTests {

    @Test
    void contextLoads() {

        JSONArray jsonArray = JSONArray.parse("[]");
        jsonArray.add(0, "0");
        jsonArray.add(1, "1");
        jsonArray.add(2, "2");
        jsonArray.add(3, "3");
        jsonArray.add(4, "4");
        jsonArray.add(6, "5");
        String string = jsonArray.toString();
        System.out.println(string);
    }

}
