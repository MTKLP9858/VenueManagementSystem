package com.klp.vms.service;

import com.klp.vms.dao.OrderDao;
import com.klp.vms.dao.StadiumDao;
import com.klp.vms.dao.Stat;
import com.klp.vms.entity.Order;
import com.klp.vms.entity.Stadium;
import com.klp.vms.entity.User;
import com.klp.vms.entity.Venue;
import com.klp.vms.exception.RuntimeError;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class OrderService {
    public static Order verifyAdminOfVenueByNumber(String accessToken, int number) throws SQLException, RuntimeError {
        User user = UserService.verifyAccessToken(accessToken);
        List<Order> orderList = new OrderDao().execQuery(number);
        Order order;
        if (orderList.size() == 1) {
            order = orderList.get(0);
        } else {
            throw new RuntimeError("Can't find this order!", 404);
        }

        //用户不能查看不是自己的订单
        if (user.getOp() == User.OP.USER) {
            if (!Objects.equals(user.getUserid(), order.getUserid())) {
                throw new RuntimeError("Permission denied", 270);
            }
        }
        //管理员不能查看不是自己场馆的订单
        if (user.getOp() == User.OP.ADMIN) {
            Stadium stadium = new StadiumDao().execQuery(order.getStadiumName());
            if (!Objects.equals(stadium.getAdminUserID(), user.getUserid())) {
                throw new RuntimeError("Permission denied", 270);
            }
        }
        return order;
    }


    public static Order query(String accessToken, int number) throws SQLException, RuntimeError {
        return verifyAdminOfVenueByNumber(accessToken, number);
    }

    /**
     * @param accessToken
     * @param userid
     * @param venueUUID
     * @param occupyStartTime
     * @param occupyEndTime
     * @param information     not require
     * @param message         not require
     */
    public static void newOrder(String accessToken, String userid, String venueUUID, long occupyStartTime, long occupyEndTime, String information, String message) throws SQLException, RuntimeError {
        User user = UserService.verifyAccessToken(accessToken);
        Order order = new Order();
        order.setNumber(new Date().getTime());
        Venue venue = VenueService.query(accessToken, venueUUID);
        if (venue == null) {
            throw new RuntimeError("No such venueUUID!", 403);
        }
        order.setUserid(userid);
        order.setStadiumName(venue.getStadium());
        order.setVenueUUID(venueUUID);

        if (user.getOp() == User.OP.ADMIN) {
            if (Objects.equals(StadiumService.getStadiumByAdminAccessToken(accessToken).getName(), venue.getStadium())) {
                order.setState(Order.STATE.PAID);
            }
        }
        if (user.getOp() == User.OP.USER) {
            if (userid == null) {
                order.setUserid(user.getUserid());
            }
            order.setState(Order.STATE.UNPAID);
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date now = new Date();
        order.setPayTime(sdf.format(now));

        Date startTime = new Date(occupyStartTime);
        Date endTime = new Date(occupyEndTime);

        if (startTime.after(now) && endTime.after(now) && endTime.after(startTime)) {
            order.setOccupyStartTime(sdf.format(startTime));
            order.setOccupyEndTime(sdf.format(endTime));
        } else {
            throw new RuntimeError("The time format is incorrect!", 533);
        }

        ////////////////////////////////////////
        //查询重复订单
        if (!new OrderDao().verifyOrderByStartTime(venueUUID, occupyStartTime, occupyEndTime).isEmpty() || !new OrderDao().verifyOrderByEndTime(venueUUID, occupyStartTime, occupyEndTime).isEmpty()) {
            throw new RuntimeError("Another user has already occupied the time period!", 500);
        }
        ////////////////////////////////////////

        order.setInformation(information);
        order.setMessage(message);

        new OrderDao().execInsert(order);
    }


}
