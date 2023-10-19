package com.klp.vms.controller;

import com.alibaba.fastjson.JSONObject;
import com.klp.vms.entity.Order;
import com.klp.vms.entity.User;
import com.klp.vms.entity.Venue;
import com.klp.vms.exception.RuntimeError;
import com.klp.vms.service.OrderService;
import com.klp.vms.service.StadiumService;
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
     * 新建订单（可以以管理员或用户身份）
     *
     * @param accessToken     管理员令牌（则订单将直接确认支付）/用户令牌（订单将成为未支付订单）
     * @param userid          用户名（顾客名字，仅用于订单，不建议与数据库用户列表不同）
     * @param venueName       场地的名字（场地号）
     * @param venueArea       场地所属区域（区域号）
     * @param stadium         场地所属场馆（管理员需验证，用户无需）
     * @param occupyStartTime 开始占用时间（开始打球的时间）
     * @param occupyEndTime   结束占用时间（停止打球的时间）
     * @param information     仅用于显示，可后期更改，建议为手机号，增值业务等备注，(required = false)
     * @param message         仅用于显示，可后期更改，建议为用户留言，(required = false)
     * @return <p>返回带有多个变量的json对象</p>
     * <li>成功：
     * <p>success=true</p>
     * <p>number:订单号</p>
     * <p>userid:用户id</p>
     * <p>stadiumName:场馆名字</p>
     * <p>venueUUID:场地的UUID，可以通过此UUID查询到场地的所有信息</p>
     * <p>state:订单状态</p>
     * <p>payTime:下单时间，不支持修改，类型为long</p>
     * <p>occupyStartTime:开始占用时间，类型为long</p>
     * <p>occupyEndTime:结束占用时间，类型为long</p>
     * <p>information:信息</p>
     * <p>message:留言</p>
     * </li>
     * <li>失败：success=false</li>
     * <li>详细信息见message</li>
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

    //update "userid", "occupyStartTime", "occupyEndTime", "information", "message"
    //updateVenue
    //ConfirmOrder
    //RefundRequest
    //RefundConfirm
    //RefundRefuse
}