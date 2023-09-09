package com.klp.vms;

import com.klp.vms.dao.VenueDao;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.SQLException;
import java.util.UUID;

@SpringBootTest
class VenueManagementSystemApplicationTests {

    @Test
    void contextLoads() throws SQLException {

       String str= new VenueDao().getUUID("12","12","11");
        System.out.println(str);
    }

}
