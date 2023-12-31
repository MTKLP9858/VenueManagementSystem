package com.klp.vms.dao;

import com.klp.vms.entity.Order;
import com.klp.vms.exception.RuntimeError;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class OrderDao implements Dao<Order> {
    public static long UNPAIDTimeOut = 20 * 60 * 1000;//20min
    public static long PAYINGTimeOut = 10 * 60 * 1000;//10min
    public static long PAYINGDeadTimeOut = 20 * 60 * 1000;//10min

    @Override
    public int execInsert(Order order) throws SQLException {
        String sql = "insert into \"Order\" (number, userid, stadiumName, venueUUID, state, payTime, occupyStartTime, occupyEndTime, information, message) VALUES (?,?,?,?,?,?,?,?,?,?);";
        try (Stat stat = new Stat(sql)) {
            stat.setDouble(1, order.getNumber());
            stat.setString(2, order.getUserid());
            stat.setString(3, order.getStadiumName());
            stat.setString(4, order.getVenueUUID());
            stat.setString(5, order.getState());
            stat.setString(6, order.getPayTime());
            stat.setString(7, order.getOccupyStartTime());
            stat.setString(8, order.getOccupyEndTime());
            stat.setString(9, order.getInformation());
            stat.setString(10, order.getMessage());
            return stat.executeUpdate();
        }
    }

    @Override
    public int execDelete(@NotNull String number) throws SQLException {
        try (Stat stat = new Stat("delete FROM \"Order\" where number=?;")) {
            stat.setString(1, number);
            return stat.executeUpdate();
        }
    }

    public int execDelete(long number) throws SQLException {
        return execDelete(String.valueOf(number));
    }

    public ArrayList<Order> verifyOrderByTimeZone(String venueUUID, long fromTime, long toTime) throws SQLException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String from = sdf.format(new java.util.Date(fromTime));
        String to = sdf.format(new Date(toTime));

        ArrayList<Order> list = new ArrayList<>();
        try (Stat stat = new Stat("select * from \"Order\" where venueUUID = ? and (occupyStartTime < ? and occupyEndTime > ?)")) {
            stat.setString(1, venueUUID);
            stat.setString(2, from);
            stat.setString(3, to);
            ResultSet rs = stat.executeQuery();
            while (rs.next()) {
                Order order = new Order();
                order.setNumber(rs.getLong("number"));
                order.setUserid(rs.getString("userid"));
                order.setStadiumName(rs.getString("stadiumName"));
                order.setVenueUUID(rs.getString("venueUUID"));
                order.setState(rs.getString("state"));
                order.setPayTime(rs.getString("payTime"));
                order.setOccupyStartTime(rs.getString("occupyStartTime"));
                order.setOccupyEndTime(rs.getString("occupyEndTime"));
                order.setInformation(rs.getString("information"));
                order.setMessage(rs.getString("message"));
                list.add(order);
            }
        }
        return list;
    }


    public ArrayList<Order> queryOrderInStadiumByTimeZone(String StadiumName, long fromTime, long toTime) throws SQLException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String from = sdf.format(new java.util.Date(fromTime));
        String to = sdf.format(new Date(toTime));

        ArrayList<Order> list = new ArrayList<>();
        try (Stat stat = new Stat("select * from \"Order\" where stadiumName = ? and (occupyStartTime < ? and occupyEndTime > ?)")) {
            stat.setString(1, StadiumName);
            stat.setString(2, from);
            stat.setString(3, to);
            ResultSet rs = stat.executeQuery();
            while (rs.next()) {
                Order order = new Order();
                order.setNumber(rs.getLong("number"));
                order.setUserid(rs.getString("userid"));
                order.setStadiumName(rs.getString("stadiumName"));
                order.setVenueUUID(rs.getString("venueUUID"));
                order.setState(rs.getString("state"));
                order.setPayTime(rs.getString("payTime"));
                order.setOccupyStartTime(rs.getString("occupyStartTime"));
                order.setOccupyEndTime(rs.getString("occupyEndTime"));
                order.setInformation(rs.getString("information"));
                order.setMessage(rs.getString("message"));
                list.add(order);
            }
        }
        return list;
    }


    public ArrayList<Order> verifyOrderByStartTime(String venueUUID, long fromTime, long toTime) throws SQLException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String from = sdf.format(new java.util.Date(fromTime));
        String to = sdf.format(new Date(toTime));

        ArrayList<Order> list = new ArrayList<>();
        try (Stat stat = new Stat("select * from \"Order\" where venueUUID = ? and (occupyStartTime between ? and ?)")) {
            stat.setString(1, venueUUID);
            stat.setString(2, from);
            stat.setString(3, to);
            ResultSet rs = stat.executeQuery();
            while (rs.next()) {
                Order order = new Order();
                order.setNumber(rs.getLong("number"));
                order.setUserid(rs.getString("userid"));
                order.setStadiumName(rs.getString("stadiumName"));
                order.setVenueUUID(rs.getString("venueUUID"));
                order.setState(rs.getString("state"));
                order.setPayTime(rs.getString("payTime"));
                order.setOccupyStartTime(rs.getString("occupyStartTime"));
                order.setOccupyEndTime(rs.getString("occupyEndTime"));
                order.setInformation(rs.getString("information"));
                order.setMessage(rs.getString("message"));
                list.add(order);
            }
        }
        return list;
    }


    public ArrayList<Order> queryOrderInStadiumByStartTime(String StadiumName, long fromTime, long toTime) throws SQLException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String from = sdf.format(new java.util.Date(fromTime));
        String to = sdf.format(new Date(toTime));

        ArrayList<Order> list = new ArrayList<>();
        try (Stat stat = new Stat("select * from \"Order\" where stadiumName = ? and (occupyStartTime between ? and ?)")) {
            stat.setString(1, StadiumName);
            stat.setString(2, from);
            stat.setString(3, to);
            ResultSet rs = stat.executeQuery();
            while (rs.next()) {
                Order order = new Order();
                order.setNumber(rs.getLong("number"));
                order.setUserid(rs.getString("userid"));
                order.setStadiumName(rs.getString("stadiumName"));
                order.setVenueUUID(rs.getString("venueUUID"));
                order.setState(rs.getString("state"));
                order.setPayTime(rs.getString("payTime"));
                order.setOccupyStartTime(rs.getString("occupyStartTime"));
                order.setOccupyEndTime(rs.getString("occupyEndTime"));
                order.setInformation(rs.getString("information"));
                order.setMessage(rs.getString("message"));
                list.add(order);
            }
        }
        return list;
    }


    public ArrayList<Order> verifyOrderByEndTime(String venueUUID, long fromTime, long toTime) throws SQLException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        java.util.Date now = new java.util.Date();
        String from = sdf.format(new java.util.Date(fromTime));
        String to = sdf.format(new Date(toTime));

        ArrayList<Order> list = new ArrayList<>();
        try (Stat stat = new Stat("select * from \"Order\" where venueUUID = ? and (occupyEndTime between ? and ?)")) {
            stat.setString(1, venueUUID);
            stat.setString(2, from);
            stat.setString(3, to);
            ResultSet rs = stat.executeQuery();
            while (rs.next()) {
                Order order = new Order();
                order.setNumber(rs.getLong("number"));
                order.setUserid(rs.getString("userid"));
                order.setStadiumName(rs.getString("stadiumName"));
                order.setVenueUUID(rs.getString("venueUUID"));
                order.setState(rs.getString("state"));
                order.setPayTime(rs.getString("payTime"));
                order.setOccupyStartTime(rs.getString("occupyStartTime"));
                order.setOccupyEndTime(rs.getString("occupyEndTime"));
                order.setInformation(rs.getString("information"));
                order.setMessage(rs.getString("message"));
                list.add(order);
            }
        }
        return list;
    }


    public ArrayList<Order> queryOrderInStadiumByEndTime(String StadiumName, long fromTime, long toTime) throws SQLException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        java.util.Date now = new java.util.Date();
        String from = sdf.format(new java.util.Date(fromTime));
        String to = sdf.format(new Date(toTime));

        ArrayList<Order> list = new ArrayList<>();
        try (Stat stat = new Stat("select * from \"Order\" where stadiumName = ? and (occupyEndTime between ? and ?)")) {
            stat.setString(1, StadiumName);
            stat.setString(2, from);
            stat.setString(3, to);
            ResultSet rs = stat.executeQuery();
            while (rs.next()) {
                Order order = new Order();
                order.setNumber(rs.getLong("number"));
                order.setUserid(rs.getString("userid"));
                order.setStadiumName(rs.getString("stadiumName"));
                order.setVenueUUID(rs.getString("venueUUID"));
                order.setState(rs.getString("state"));
                order.setPayTime(rs.getString("payTime"));
                order.setOccupyStartTime(rs.getString("occupyStartTime"));
                order.setOccupyEndTime(rs.getString("occupyEndTime"));
                order.setInformation(rs.getString("information"));
                order.setMessage(rs.getString("message"));
                list.add(order);
            }
        }
        return list;
    }


    @Override
    public List<Order> execQuery(String column, Object value) throws SQLException, ParseException {
        String sql = "select * from \"Order\" where " + column + "=?;";
        ArrayList<Order> list = new ArrayList<>();
        try (Stat stat = new Stat(sql)) {
            stat.setObject(1, value);
            ResultSet rs = stat.executeQuery();
            while (rs.next()) {
                Order order = new Order();
                order.setNumber(rs.getLong("number"));
                order.setUserid(rs.getString("userid"));
                order.setStadiumName(rs.getString("stadiumName"));
                order.setVenueUUID(rs.getString("venueUUID"));
                order.setState(rs.getString("state"));
                order.setPayTime(rs.getString("payTime"));
                order.setOccupyStartTime(rs.getString("occupyStartTime"));
                order.setOccupyEndTime(rs.getString("occupyEndTime"));
                order.setInformation(rs.getString("information"));
                order.setMessage(rs.getString("message"));
                list.add(order);
            }
        }


        for (Order order : list) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date payTime = sdf.parse(order.getPayTime());
            Date occupyStartTime = sdf.parse(order.getOccupyStartTime());
            Date occupyEndTime = sdf.parse(order.getOccupyEndTime());

            if (Objects.equals(order.getState(), Order.STATE.UNPAID)) {
                //如果超时
                if ((payTime.getTime() + UNPAIDTimeOut) < new Date().getTime()) {
                    execDelete(order.getNumber());
                    list.remove(order);
                }
            }
            if (Objects.equals(order.getState(), Order.STATE.PAYING)) {
                //如果超时
                if ((payTime.getTime() + PAYINGTimeOut) < new Date().getTime()) {
                    execUpdate("state", Order.STATE.UNPAID, order.getNumber());
                    order.setState(Order.STATE.UNPAID);
                }
                if ((payTime.getTime() + PAYINGDeadTimeOut) < new Date().getTime()) {
                    execDelete(order.getNumber());
                    list.remove(order);
                }
            }
            if (Objects.equals(order.getState(), Order.STATE.PAID)) {
                //如果到达开始时间
                if ((occupyStartTime.getTime()) < new Date().getTime() && (occupyEndTime.getTime()) > new Date().getTime()) {
                    execUpdate("state", Order.STATE.USING, order.getNumber());
                    order.setState(Order.STATE.USING);
                }
                if ((occupyEndTime.getTime()) < new Date().getTime()) {
                    execUpdate("state", Order.STATE.DONE, order.getNumber());
                    order.setState(Order.STATE.DONE);
                }
            }
            if (Objects.equals(order.getState(), Order.STATE.USING)) {
                //如果到达开始时间
                if ((occupyEndTime.getTime()) < new Date().getTime()) {
                    execUpdate("state", Order.STATE.DONE, order.getNumber());
                    order.setState(Order.STATE.DONE);
                }
            }
        }

        return list;
    }

    public List<Order> execQuery(long number) throws SQLException, ParseException {
        return execQuery("number", String.valueOf(number));
    }

    @Override
    public int execUpdate(String column, Object value, String number) throws SQLException {
        String sql = "UPDATE \"Order\" SET " + column + "=? WHERE number=?";
        try (Stat stat = new Stat(sql)) {
            stat.setObject(1, value);
            stat.setString(2, number);
            return stat.executeUpdate();
        }
    }

    public int execUpdate(String column, Object value, long number) throws SQLException {
        return execUpdate(column, value, String.valueOf(number));
    }


    public int execUpdate(String numberColumn, long value, long number) throws SQLException {
        if (Objects.equals(numberColumn, "number")) {
            return execUpdate(numberColumn, String.valueOf(value), String.valueOf(number));
        } else {
            return -1;
        }
    }
}
