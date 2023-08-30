package com.klp.vms.dao;

import com.klp.vms.entity.Order;
import com.klp.vms.exception.RuntimeError;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OrderDao implements Dao<Order> {
    @Override
    public void execInsert(Order order) throws RuntimeError {
        StringBuilder sql = new StringBuilder("insert into \"Order\" (number, userid, stadiumName, venueName, state, payTime, occupyStartTime, occupyEndTime, information, message) VALUES (");
        sql.append(order.getNumber() < 0 ? order.getNumber() * -1 : order.getNumber()).append(",");
        sql.append(order.getUserid() == null ? "NULL" : ("'" + order.getUserid().replaceAll("'", "''") + "'")).append(",");
        sql.append(order.getStadiumName() == null ? "NULL" : ("'" + order.getStadiumName().replaceAll("'", "''") + "'")).append(",");
        sql.append(order.getVenueName() == null ? "NULL" : ("'" + order.getVenueName().replaceAll("'", "''") + "'")).append(",");
        sql.append(order.getState() == null ? "NULL" : ("'" + order.getState().replaceAll("'", "''") + "'")).append(",");
        sql.append(order.getPayTime() == null ? "NULL" : ("'" + order.getPayTime().replaceAll("'", "''") + "'")).append(",");
        sql.append(order.getOccupyStartTime() == null ? "NULL" : ("'" + order.getOccupyStartTime().replaceAll("'", "''") + "'")).append(",");
        sql.append(order.getOccupyEndTime() == null ? "NULL" : ("'" + order.getOccupyEndTime().replaceAll("'", "''") + "'")).append(",");
        sql.append(order.getInformation() == null ? "NULL" : ("'" + order.getInformation().replaceAll("'", "''") + "'")).append(",");
        sql.append(order.getMessage() == null ? "NULL" : ("'" + order.getMessage().replaceAll("'", "''") + "'"));
        sql.append(");");
        this.update(String.valueOf(sql));
    }

    @Override
    public void execDelete(String number) throws RuntimeError {
        this.update("delete FROM \"Order\" where number='" + number.replaceAll("'", "''") + "';");
    }

    public void execDelete(long number) throws RuntimeError {
        execDelete(String.valueOf(number));
    }

    @Override
    public List<Order> execQuery(String column, String value) throws SQLException, RuntimeError {
        if (value == null) return null;
        String sql = "select * from \"Order\" where " + column.replaceAll("'", "''") + "='" + value.replaceAll("'", "''") + "';";
        ArrayList<Order> list = new ArrayList<>();
        ResultSet rs = this.query(sql);
        while (rs.next()) {
            Order order = new Order();
            order.setNumber(rs.getLong("number"));
            order.setUserid(rs.getString("userid"));
            order.setStadiumName(rs.getString("stadiumName"));
            order.setVenueName(rs.getString("venueName"));
            order.setState(rs.getString("state"));
            order.setPayTime(rs.getString("payTime"));
            order.setOccupyStartTime(rs.getString("occupyStartTime"));
            order.setOccupyEndTime(rs.getString("occupyEndTime"));
            order.setInformation(rs.getString("information"));
            order.setMessage(rs.getString("message"));
            list.add(order);
        }
        this.close();
        return list;
    }

    public List<Order> execQuery(long number) throws SQLException, RuntimeError {
        return execQuery("number", String.valueOf(number));
    }

    @Override
    public void execUpdate(String column, String value, String number) throws RuntimeError {
        StringBuilder sql = new StringBuilder("UPDATE \"Order\" SET ");
        sql.append(column.replaceAll("'", "''") + "=");
        sql.append("'" + value.replaceAll("'", "''") + "'");
        sql.append(" WHERE number=");
        sql.append(number);
        this.update(String.valueOf(sql));
    }

    public void execUpdate(String column, String value, long number) throws RuntimeError {
        execUpdate(column, value, String.valueOf(number));
    }


    public void execUpdate(String numberColumn, long value, long number) throws RuntimeError {
        if (Objects.equals(numberColumn, "number"))
            execUpdate(numberColumn, String.valueOf(value), String.valueOf(number));
    }
}
