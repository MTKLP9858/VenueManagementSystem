package com.klp.vms.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.klp.vms.entity.Venue;
import com.klp.vms.exception.RuntimeError;
import com.klp.vms.service.VenueService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

@Controller
@RestController
@RequestMapping("/venue")
public class VenueController {
    /**
     * @param accessToken  an access token of admin or su
     * @param name         venue name
     * @param area         the area of this venue
     * @param stadium      the stadium of this venue
     * @param price        a number
     * @param introduction not required, an introduction
     * @return a {@link String} serialization by {@link Venue}
     */
    @PostMapping("/add")
    public String add(@RequestHeader String accessToken, @RequestParam String name, @RequestParam String area, @RequestParam String stadium, @RequestParam double price, @RequestParam(required = false) String introduction) {
        try {
            VenueService.add(accessToken, name, area, stadium, price, introduction);
        } catch (SQLException e) {
            JSONObject json = new JSONObject();
            json.put("code", 9);
            json.put("success", false);
            json.put("message", e.getMessage());
            return json.toString();
        } catch (RuntimeError e) {
            JSONObject json = new JSONObject();
            json.put("code", e.getCode());
            json.put("success", false);
            json.put("message", e.getMessage());
            return json.toString();
        }
        JSONObject json = new JSONObject();
        json.put("code", 220);
        json.put("success", true);
        json.put("message", "add venue success!");
        return json.toString();
    }

    @PostMapping("/delete")
    public String delete(@RequestHeader String accessToken, @RequestParam String name, @RequestParam String area, @RequestParam String stadium) {
        try {
            VenueService.delete(accessToken, name, area, stadium);
        } catch (SQLException e) {
            JSONObject json = new JSONObject();
            json.put("code", 9);
            json.put("success", false);
            json.put("message", e.getMessage());
            return json.toString();
        } catch (RuntimeError e) {
            JSONObject json = new JSONObject();
            json.put("code", e.getCode());
            json.put("success", false);
            json.put("message", e.getMessage());
            return json.toString();
        }
        JSONObject json = new JSONObject();
        json.put("code", 220);
        json.put("success", true);
        json.put("message", "delete venue success!");
        return json.toString();
    }

    @PostMapping("/query")
    public String query(@RequestParam String name, @RequestParam String area, @RequestParam String stadium) {
        Venue venue;
        try {
            venue = VenueService.query(name, area, stadium);
        } catch (SQLException e) {
            JSONObject json = new JSONObject();
            json.put("code", 9);
            json.put("success", false);
            json.put("message", e.getMessage());
            return json.toString();
        }
        JSONObject json = JSON.parseObject(venue.toString());
        json.put("code", 220);
        json.put("success", true);
        return json.toString();
    }


}
