package com.klp.vms.dao;

import com.klp.vms.entity.Order;
import com.klp.vms.entity.User;
import com.klp.vms.entity.Venue;
import com.klp.vms.exception.RuntimeError;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OrderDao implements Dao<Order> {
    Statement statement;
    Connection connection;

    @Override
    public void connect() throws Exception {
        connect(defaultDataBaseUrl);
    }

    @Override
    public void connect(String dataBaseName) throws RuntimeError {
        try {
            statement.close();
            connection.close();
        } catch (Exception ignored) {
        }
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeError("Missing org.sqlite.JDBC, please check the server environment!", 10);
        }
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + dataBaseName);
            statement = connection.createStatement();
        } catch (SQLException e) {
            throw new RuntimeError("Database error, check if the database path or data table exists", 11);
        }
    }

    @Override
    public void disConnect() {
        try {
            statement.close();
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void execInsert(Order order) throws SQLException {
        StringBuilder sql = new StringBuilder("insert into \"Order\" (number, userid, stadiumName, venueName, state, payTime, occupyStartTime, occupyEndTime, information, message) VALUES (");
        sql.append(order.getNumber() < 0 ? order.getNumber() * -1 : order.getNumber()).append(",");
        sql.append(order.getUserid() == null ? "NULL" : ("'" + order.getUserid() + "'")).append(",");
        sql.append(order.getStadiumName() == null ? "NULL" : ("'" + order.getStadiumName() + "'")).append(",");
        sql.append(order.getVenueName() == null ? "NULL" : ("'" + order.getVenueName() + "'")).append(",");
        sql.append(order.getState() == null ? "NULL" : ("'" + order.getState() + "'")).append(",");
        sql.append(order.getPayTime() == null ? "NULL" : ("'" + order.getPayTime() + "'")).append(",");
        sql.append(order.getOccupyStartTime() == null ? "NULL" : ("'" + order.getOccupyStartTime() + "'")).append(",");
        sql.append(order.getOccupyEndTime() == null ? "NULL" : ("'" + order.getOccupyEndTime() + "'")).append(",");
        sql.append(order.getInformation() == null ? "NULL" : ("'" + order.getInformation() + "'")).append(",");
        sql.append(order.getMessage() == null ? "NULL" : ("'" + order.getMessage() + "'"));
        sql.append(");");
        statement.executeUpdate(String.valueOf(sql));
    }

    @Override
    public void execDelete(String number) throws SQLException {
        statement.executeUpdate("delete FROM \"Order\" where number='" + number + "';");

    }

    @Override
    public List<Order> execQuery(String column, String value) throws SQLException {
        if (value == null) return null;
        String sql = "select * from \"Order\" where " + column + "='" + value + "';";
        ResultSet rs = statement.executeQuery(sql);
        ArrayList<Order> list = new ArrayList<>();
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
        return list;
    }

    public List<Order> execQuery(long number) throws SQLException {
        return execQuery("number", String.valueOf(number));
    }

    @Override
    public void execUpdate(String column, String value, String number) throws SQLException {
        StringBuilder sql = new StringBuilder("UPDATE \"Order\" SET ");
        sql.append(column + "=");
        sql.append("'" + value + "'");
        sql.append(" WHERE number=");
        sql.append(number);
        statement.executeUpdate(String.valueOf(sql));
    }

    public void execUpdate(String column, String value, long number) throws SQLException {
        execUpdate(column, value, String.valueOf(number));
    }


    public void execUpdate(String numberColumn, long value, long number) throws SQLException {
        if (Objects.equals(numberColumn, "number"))
            execUpdate(numberColumn, String.valueOf(value), String.valueOf(number));
    }
}
