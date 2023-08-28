package com.klp.vms.dao;

import com.klp.vms.entity.Order;
import com.klp.vms.entity.User;
import com.klp.vms.exception.RuntimeError;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

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
        return null;
    }

    @Override
    public void execUpdate(String column, String value, String KeyColumn) throws SQLException {

    }
}
