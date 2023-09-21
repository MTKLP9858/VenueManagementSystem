package com.klp.vms.service;

import com.klp.vms.dao.OrderDao;
import com.klp.vms.dao.StadiumDao;
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
     * @param state
     */
    public static void newOrder(String accessToken, String userid, String venueUUID, String state) throws SQLException, RuntimeError {
        User user = UserService.verifyAccessToken(accessToken);
        if (user.getOp() == User.OP.ADMIN) {
            Venue venue = VenueService.query(accessToken, venueUUID);
            Stadium stadium = StadiumService.getStadiumName(accessToken);
            if (Objects.equals(stadium.getAdminUserID(), user.getUserid())) {
                Order order = new Order();
                order.setUserid(userid);
                order.setStadiumName(venue.getStadium());
                order.setVenueUUID(venueUUID);

                order.setState(Order.STATE.PAID);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                order.setPayTime(sdf.format(new Date()));

            }
        }
    }

    public static void updatePayTime(String accessToken, int number) {

    }


}
