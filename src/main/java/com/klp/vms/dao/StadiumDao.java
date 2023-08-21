package com.klp.vms.dao;

import com.klp.vms.entity.Stadium;
import com.klp.vms.entity.User;
import com.klp.vms.exception.RuntimeError;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StadiumDao implements Dao<Stadium> {
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
    public void execInsert(Stadium stadium) throws SQLException {

    }

    @Override
    public void execDelete(String name) throws SQLException {
        statement.executeUpdate("delete FROM Stadium where name='" + name + "';");
    }

    @Override
    public List<Stadium> execQuery(String column, String value) throws SQLException, RuntimeError {
        if (value == null) return null;
        String sql = "select * from Stadium where " + column + "='" + value + "';";
        ResultSet rs = statement.executeQuery(sql);
        ArrayList<Stadium> list = new ArrayList<>();
        while (rs.next()) {
            Stadium stadium = new Stadium();
            stadium.setName(rs.getString("name"));
            stadium.setAddress(rs.getString("address"));
            stadium.setIntroduction(rs.getString("introduction"));
            stadium.setContact(rs.getString("contact"));
            stadium.setAdminUserID(rs.getString("adminUserID"));
            UserDao userDao = new UserDao();
            stadium.setAdminUser(userDao.execQuery(stadium.getAdminUserID()));
            userDao.disConnect();
            ///////////////////////////
            // 用场馆的名字来获取表Venue，以及Venue的KEY：name
            // Hashmap封装
            ///////////////////////////
            list.add(stadium);
        }
        return list;
    }

    @Override
    public void execUpdate(String column, String value, String KeyColumn) throws SQLException {

    }
}
