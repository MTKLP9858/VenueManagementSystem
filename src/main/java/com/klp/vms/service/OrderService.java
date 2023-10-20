package com.klp.vms.service;

import com.klp.vms.dao.OrderDao;
import com.klp.vms.dao.StadiumDao;
import com.klp.vms.dao.VenueDao;
import com.klp.vms.entity.Order;
import com.klp.vms.entity.Stadium;
import com.klp.vms.entity.User;
import com.klp.vms.entity.Venue;
import com.klp.vms.exception.RuntimeError;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class OrderService {

    public static void updateVenue(String accessToken, long number, String venueName, String venueArea) throws SQLException, RuntimeError, ParseException {
        Order order = verifyAdminOfVenueByNumber(accessToken, number);
        String venueUUID = new VenueDao().getUUID(venueName, venueArea, order.getStadiumName());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long startTime = sdf.parse(order.getOccupyStartTime()).getTime();
        long endTime = sdf.parse(order.getOccupyEndTime()).getTime();

        ArrayList<Order> orders = queryOrderByTime(venueUUID, startTime, endTime);

        if (orders.isEmpty()) {
            new OrderDao().execUpdate("venueUUID", venueUUID, number);
        } else {
            throw new RuntimeError("There are conflicting orders!", 180);
        }
    }

    public static void update(String accessToken, long number, String column, String value) throws SQLException, RuntimeError, ParseException {
        verifyAdminOfVenueByNumber(accessToken, number);
        if (column != null) {
            switch (column) {
                case "userid", "information", "message" -> {//限制column为Stadium的列名
                    new OrderDao().execUpdate(column, value, number);
                }
                case "occupyStartTime" -> {
                    Order order = OrderService.query(accessToken, number);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    long endTime = sdf.parse(order.getOccupyEndTime()).getTime();
                    ArrayList<Order> orders = queryOrderByTime(order.getVenueUUID(), sdf.parse(value).getTime(), endTime);
                    if (orders.isEmpty()) {
                        new OrderDao().execUpdate(column, value, number);
                    } else {
                        throw new RuntimeError("There are conflicting orders!", 180);
                    }
                }
                case "occupyEndTime" -> {
                    Order order = OrderService.query(accessToken, number);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    long startTime = sdf.parse(order.getOccupyStartTime()).getTime();
                    ArrayList<Order> orders = queryOrderByTime(order.getVenueUUID(), startTime, sdf.parse(value).getTime());
                    if (orders.isEmpty()) {
                        new OrderDao().execUpdate(column, value, number);
                    } else {
                        throw new RuntimeError("There are conflicting orders!", 180);
                    }
                }
                default -> throw new RuntimeError("Illegal column!", 283);
            }
        }
    }

    public static Order verifyAdminOfVenueByNumber(String accessToken, long number) throws SQLException, RuntimeError, ParseException {
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


    public static Order query(String accessToken, long number) throws SQLException, RuntimeError, ParseException {
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
     * @return
     */
    public static Order newOrder(String accessToken, String userid, String venueUUID, long occupyStartTime, long occupyEndTime, String information, String message) throws SQLException, RuntimeError, ParseException {
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

        if (endTime.after(now) && endTime.after(startTime)) {
            order.setOccupyStartTime(sdf.format(startTime));
            order.setOccupyEndTime(sdf.format(endTime));
        } else {
            System.out.println(now);
            System.out.println(startTime);
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
        return order;
    }

    public static ArrayList<Order> queryOrderByTime(String venueUUID, long startTime, long endTime) throws SQLException {
        ArrayList<Order> orders = new OrderDao().verifyOrderByStartTime(venueUUID, startTime, endTime);
        ArrayList<Order> ordersAdder = new OrderDao().verifyOrderByEndTime(venueUUID, startTime, endTime);
        for (Order oa : ordersAdder) {
            if (!orders.contains(oa)) {
                orders.add(oa);
            }
        }
        return orders;
    }

    public static void ConfirmOrder(long number) throws SQLException {
//没有支付验证，该功能异常
    }


    public static void RefundRequest(long number) throws SQLException {

    }

    public static void RefundConfirm(long number) throws SQLException {

    }

    public static void RefundRefuse(long number) throws SQLException {

    }

    public static List<Order> queryOrderByVenueUUID(String UUID) throws SQLException, ParseException {
        return new OrderDao().execQuery("venueUUID", UUID);
    }

    public static List<Order> StateFilter(List<Order> orders, String[] states) throws SQLException {
        List<Order> orderList = new ArrayList<>();
        for (Order order : orders) {
            if (Arrays.asList(states).contains(order.getState())) {
                orderList.add(order);
            }
        }
        return orderList;
    }


}