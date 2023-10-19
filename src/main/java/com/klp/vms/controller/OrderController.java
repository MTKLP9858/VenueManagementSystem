package com.klp.vms.controller;

import com.alibaba.fastjson.JSONObject;
import com.klp.vms.entity.Order;
import com.klp.vms.entity.User;
import com.klp.vms.entity.Venue;
import com.klp.vms.exception.RuntimeError;
import com.klp.vms.service.OrderService;
import com.klp.vms.service.UserService;
import com.klp.vms.service.VenueService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;

@Controller
@RestController
@RequestMapping("/order")
public class OrderController {
    /**
     * @param accessToken
     * @param userid
     * @param venueName
     * @param venueArea
     * @param stadium
     * @param occupyStartTime
     * @param occupyEndTime
     * @param information
     * @param message
     * @return
     */
    @PostMapping("/newOrder")
    public String newOrder(@RequestHeader String accessToken, @RequestParam String userid, @RequestParam String venueName, @RequestParam String venueArea, @RequestParam String stadium, @RequestParam long occupyStartTime, @RequestParam long occupyEndTime, @RequestParam(required = false) String information, @RequestParam(required = false) String message) {
        Order order;
        Venue venue;
        try {
            String venueUUID = VenueService.getUUID(venueName, venueArea, stadium);
            order = OrderService.newOrder(accessToken, userid, venueUUID, occupyStartTime, occupyEndTime, information, message);
            venue = VenueService.query(order.getVenueUUID());
        } catch (SQLException | ParseException e) {
            JSONObject json = new JSONObject();
            json.put("code", 9);
            json.put("success", false);
            json.put("message", e.getMessage());
            return json.toString();
        } catch (RuntimeError e) {
            return e.toString();
        }
        JSONObject json = (JSONObject) JSONObject.parse(order.toString());
        json.put("venueName", venue.getName());
        json.put("venueArea", venue.getArea());
        json.put("stadium", venue.getStadium());
        json.put("code", 200);
        json.put("success", true);
        json.put("message", "A new order is created and the order number is " + order.getNumber());
        return json.toString();

    }

    @PostMapping("/queryOrder")
    public String queryOrderByTime(@RequestHeader String accessToken, @RequestParam String venueName, @RequestParam String venueArea, @RequestParam String stadium, @RequestParam long occupyStartTime, @RequestParam long occupyEndTime) {
        ArrayList<Order> orders = null;
        try {
            User user = UserService.verifyAccessToken(accessToken);
            if (user.getOp() == User.OP.USER) {
                throw new RuntimeError("You are not an administrator! Permission denied!", 271);
            }
            if (user.getOp() == User.OP.ADMIN) {
                String venueUUID = VenueService.getUUID(venueName, venueArea, stadium);
                VenueService.verifyAdminOfVenueByUUID(accessToken, venueUUID);
                orders = OrderService.queryOrderByTime(venueUUID, occupyStartTime, occupyEndTime);
            }
        } catch (SQLException | ParseException e) {
            JSONObject json = new JSONObject();
            json.put("code", 9);
            json.put("success", false);
            json.put("message", e.getMessage());
            return json.toString();
        } catch (RuntimeError e) {
            return e.toString();
        }
        JSONObject json = new JSONObject();
        if (orders != null) {
            json = (JSONObject) JSONObject.parse(orders.toString());
        }
        json.put("code", 200);
        json.put("success", true);
        return json.toString();

    }
}