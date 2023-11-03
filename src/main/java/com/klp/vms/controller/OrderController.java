package com.klp.vms.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.klp.vms.entity.Order;
import com.klp.vms.entity.User;
import com.klp.vms.entity.Venue;
import com.klp.vms.exception.RuntimeError;
import com.klp.vms.service.OrderService;
import com.klp.vms.service.UserService;
import com.klp.vms.service.VenueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;

@Slf4j//TODO
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

    /**
     * 查询这个场地，一段时间内的订单
     *
     * @param accessToken     管理员令牌
     * @param venueName       需要查询的场地名字
     * @param venueArea       需要查询的场地区域
     * @param stadium         需要查询的场馆名字，用作验证
     * @param occupyStartTime 订单开始时间[long]
     * @param occupyEndTime   订单结束时间[long]
     * @return 返回一个包含order对象的jsonArray字符串，若返回0个订单，则应返回"[]" 。
     * 先用jsonArray解析可得到一些jsonObject，接着解析jsonObject可得到单个订单的信息。
     * 示例：
     * [{"number":1697701757615, "userid":"user1", "stadiumName":"stadium1", "venueUUID":"063fd8c6-097e-49a0-b2ea-93bded6b43d4", "state":"已完成", "payTime":1697701757000, "occupyStartTime":1697701757000, "occupyEndTime":1697702117000, "information":"infor111111111", "message":"message2222222222"},
     * {"number":1697702247822, "userid":"user1", "stadiumName":"stadium1", "venueUUID":"063fd8c6-097e-49a0-b2ea-93bded6b43d4", "state":"已完成", "payTime":1697702247000, "occupyStartTime":1697702247000, "occupyEndTime":1697702847000, "information":"infor111111111", "message":"message2222222222"},
     * {"number":1697702299674, "userid":"user1", "stadiumName":"stadium1", "venueUUID":"063fd8c6-097e-49a0-b2ea-93bded6b43d4", "state":"已完成", "payTime":1697702299000, "occupyStartTime":1697702899000, "occupyEndTime":1697703499000, "information":"infor111111111", "message":"message2222222222"},
     * {"number":1697771551167, "userid":"user1", "stadiumName":"stadium1", "venueUUID":"063fd8c6-097e-49a0-b2ea-93bded6b43d4", "state":"已完成", "payTime":1697771551000, "occupyStartTime":1697772150000, "occupyEndTime":1697772270000, "information":"infor111111111", "message":"message2222222222"},
     * {"number":1697772399387, "userid":"user1", "stadiumName":"stadium1", "venueUUID":"063fd8c6-097e-49a0-b2ea-93bded6b43d4", "state":"已支付", "payTime":1697772399000, "occupyStartTime":1697772999000, "occupyEndTime":1697773599000, "information":"infor111111111", "message":"message2222222222"}]
     */
    @PostMapping("/queryOrderByTime")
    public String queryOrderByTime(@RequestHeader String accessToken, @RequestParam String venueName, @RequestParam String venueArea, @RequestParam String stadium, @RequestParam long occupyStartTime, @RequestParam long occupyEndTime) {
        ArrayList<Order> orders = null;
        try {
            User user = UserService.verifyAccessToken(accessToken);
            if (user.getOp() == User.OP.USER) {
                throw new RuntimeError("You are not an administrator! Permission denied!", 271);
            } else if (user.getOp() == User.OP.ADMIN) {
                String venueUUID = VenueService.getUUID(venueName, venueArea, stadium);
                VenueService.verifyAdminOfVenueByUUID(accessToken, venueUUID);
                orders = OrderService.queryOrderByTime(venueUUID, occupyStartTime, occupyEndTime);
            } else if (user.getOp() == User.OP.SU) {
                String venueUUID = VenueService.getUUID(venueName, venueArea, stadium);
                orders = OrderService.queryOrderByTime(venueUUID, occupyStartTime, occupyEndTime);
            } else {
                throw new RuntimeError("You don't have a OP?", 403);
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
        JSONArray jsonArray = (JSONArray) JSONArray.parse(orders.toString());
        return jsonArray.toString();
    }


    /**
     * 查询这个场馆，一段时间内的订单（列表）
     *
     * @param accessToken     管理员令牌
     * @param stadium         需要查询的场馆名字
     * @param occupyStartTime 订单开始时间[long]
     * @param occupyEndTime   订单结束时间[long]
     * @return 返回一个包含order对象的jsonArray字符串，若返回0个订单，则应返回"[]" 。
     * 先用jsonArray解析可得到一些jsonObject，接着解析jsonObject可得到单个订单的信息。
     * 示例：
     * [{"number":1697701757615, "userid":"user1", "stadiumName":"stadium1", "venueUUID":"063fd8c6-097e-49a0-b2ea-93bded6b43d4", "state":"已完成", "payTime":1697701757000, "occupyStartTime":1697701757000, "occupyEndTime":1697702117000, "information":"infor111111111", "message":"message2222222222"},
     * {"number":1697702247822, "userid":"user1", "stadiumName":"stadium1", "venueUUID":"063fd8c6-097e-49a0-b2ea-93bded6b43d4", "state":"已完成", "payTime":1697702247000, "occupyStartTime":1697702247000, "occupyEndTime":1697702847000, "information":"infor111111111", "message":"message2222222222"},
     * {"number":1697702299674, "userid":"user1", "stadiumName":"stadium1", "venueUUID":"063fd8c6-097e-49a0-b2ea-93bded6b43d4", "state":"已完成", "payTime":1697702299000, "occupyStartTime":1697702899000, "occupyEndTime":1697703499000, "information":"infor111111111", "message":"message2222222222"},
     * {"number":1697771551167, "userid":"user1", "stadiumName":"stadium1", "venueUUID":"063fd8c6-097e-49a0-b2ea-93bded6b43d4", "state":"已完成", "payTime":1697771551000, "occupyStartTime":1697772150000, "occupyEndTime":1697772270000, "information":"infor111111111", "message":"message2222222222"},
     * {"number":1697772399387, "userid":"user1", "stadiumName":"stadium1", "venueUUID":"063fd8c6-097e-49a0-b2ea-93bded6b43d4", "state":"已支付", "payTime":1697772399000, "occupyStartTime":1697772999000, "occupyEndTime":1697773599000, "information":"infor111111111", "message":"message2222222222"}]
     */
    @PostMapping("/queryOrderInStadiumByTime")
    public String queryOrderInStadiumByTime(@RequestHeader String accessToken, @RequestParam String stadium, @RequestParam long occupyStartTime, @RequestParam long occupyEndTime) {
        ArrayList<Order> orders;
        try {
            User user = UserService.verifyAccessToken(accessToken);
            if (user.getOp() == User.OP.USER) {
                throw new RuntimeError("You are not an administrator! Permission denied!", 271);
            } else if (user.getOp() == User.OP.ADMIN) {
                orders = OrderService.queryOrderInStadiumByTime(stadium, occupyStartTime, occupyEndTime);
            } else if (user.getOp() == User.OP.SU) {
                orders = OrderService.queryOrderInStadiumByTime(stadium, occupyStartTime, occupyEndTime);
            } else {
                throw new RuntimeError("You don't have a OP?", 403);
            }

        } catch (SQLException e) {
            JSONObject json = new JSONObject();
            json.put("code", 9);
            json.put("success", false);
            json.put("message", e.getMessage());
            return json.toString();
        } catch (RuntimeError e) {
            return e.toString();
        }
        JSONArray jsonArray = (JSONArray) JSONArray.parse(orders.toString());
        return jsonArray.toString();
    }


    /**
     * 更新订单信息中的：
     * "userid"[String], "information"[String], "message"[String],
     * "occupyStartTime"[String(格式为：yyyy-MM-dd HH:mm:ss)], "occupyEndTime"[String(格式为：yyyy-MM-dd HH:mm:ss)]
     * 请填写于column中
     *
     * @param accessToken 管理员令牌，将会验证是否为订单中场地的管理员
     * @param number      订单号，将被提取有关信息作为冲突验证
     * @param column      需要更改的信息类型
     * @param value       需要更改的值
     * @return <p>返回带有多个变量的json对象</p>
     * <li>成功：success=true</li>
     * <li>失败：success=false</li>
     * <li>详细信息见message</li>
     */
    @PostMapping("/update")
    public String update(@RequestHeader String accessToken, @RequestParam long number, @RequestParam String column, @RequestParam String value) {
        try {
            OrderService.update(accessToken, number, column, value);
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
        json.put("code", 200);
        json.put("success", true);
        json.put("message", "Update succeed!");
        return json.toString();
    }

    /**
     * 更新订单信息中的场地信息，由于订单只能更改为馆内场地，所以只需要场地名和区域即可
     *
     * @param accessToken 管理员令牌，将会验证是否为订单中场地的管理员
     * @param number      订单号，将被提取有关信息作为冲突验证
     * @param venueName   新的场地名称值[String]
     * @param venueArea   新的场地区域值[String]
     * @return <p>返回带有多个变量的json对象</p>
     * <li>成功：success=true</li>
     * <li>失败：success=false</li>
     * <li>详细信息见message</li>
     */
    @PostMapping("/updateVenue")
    public String updateVenue(@RequestHeader String accessToken, @RequestParam long number, @RequestParam String venueName, @RequestParam String venueArea) {
        try {
            OrderService.updateVenue(accessToken, number, venueName, venueArea);
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
        json.put("code", 200);
        json.put("success", true);
        json.put("message", "Update succeed!");
        return json.toString();
    }


    //ConfirmOrder
    //RefundRequest
    //RefundConfirm
    //RefundRefuse
}