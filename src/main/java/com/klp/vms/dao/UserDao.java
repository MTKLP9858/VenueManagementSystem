package com.klp.vms.dao;

import com.klp.vms.entity.User;
import com.klp.vms.exception.RuntimeError;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.ArrayList;

public class UserDao implements Dao<User> {
    Statement statement;
    Connection connection;

    public UserDao() throws RuntimeError {
        connect();
    }

    public void disConnect() {
        try {
            statement.close();
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void connect() throws RuntimeError {
        connect(defaultDataBaseUrl);
    }

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
    public ArrayList<User> execQuery(String TYPE, String value) throws SQLException {
        if (value == null) return null;
        String sql = "select * from User where " + TYPE + "='" + value + "';";
        ResultSet rs = statement.executeQuery(sql);
        ArrayList<User> list = new ArrayList<>();
        while (rs.next()) {
            User user = new User(-1);
            user.setUserid(rs.getString("userid"));
            user.setUsername(rs.getString("username"));
            user.setPassword(rs.getString("password"));
            user.setOp(rs.getInt("op"));
            user.setAccess_token(rs.getString("access_token"));
            user.setAccess_token_age(rs.getString("access_token_age"));
            user.setRefresh_token(rs.getString("refresh_token"));
            user.setRefresh_token_age(rs.getString("refresh_token_age"));
            list.add(user);
        }
        return list;
    }

    public User execQueryBy(String column, String value) throws SQLException {
        if (value == null) return null;
        String sql = "select * from User where " + column + "='" + value + "';";
        ResultSet rs = statement.executeQuery(sql);
        User user = new User(-1);
        if (rs.next()) {
            user.setUserid(rs.getString("userid"));
            user.setUsername(rs.getString("username"));
            user.setPassword(rs.getString("password"));
            user.setOp(rs.getInt("op"));
            user.setAccess_token(rs.getString("access_token"));
            user.setAccess_token_age(rs.getString("access_token_age"));
            user.setRefresh_token(rs.getString("refresh_token"));
            user.setRefresh_token_age(rs.getString("refresh_token_age"));
        }
        return user.getUserid() == null ? null : user;
    }

    public User execQuery(String userid) throws SQLException {
        return execQueryBy("userid", userid);
    }

    public void execInsert(@NotNull User user) throws SQLException {
        StringBuilder sql = new StringBuilder("insert into User (userid, username, password, op, access_token, access_token_age, refresh_token, refresh_token_age) VALUES (");
        sql.append(user.getUserid() == null ? "NULL" : ("'" + user.getUserid()) + "'").append(",");
        sql.append(user.getUsername() == null ? "NULL" : ("'" + user.getUsername()) + "'").append(",");
        sql.append(user.getPassword() == null ? "NULL" : ("'" + user.getPassword()) + "'").append(",");
        sql.append(user.getOp()).append(",");
        sql.append(user.getAccess_token() == null ? "NULL" : ("'" + user.getAccess_token()) + "'").append(",");
        sql.append(user.getAccess_token_age() == null ? "NULL" : ("'" + user.getAccess_token_age()) + "'").append(",");
        sql.append(user.getRefresh_token() == null ? "NULL" : ("'" + user.getRefresh_token()) + "'").append(",");
        sql.append(user.getRefresh_token_age() == null ? "NULL" : ("'" + user.getRefresh_token_age()) + "'");
        sql.append(");");
        statement.executeUpdate(String.valueOf(sql));
    }

    public void execDelete(String userid) throws SQLException {
        statement.executeUpdate("delete FROM User where userid='" + userid + "';");
    }

    public void execUpdate(String column, String value, String userid) throws SQLException {
        StringBuilder sql = new StringBuilder("UPDATE User SET ");
        sql.append(column + "=");
        sql.append("'" + value + "'");
        sql.append(" WHERE userid=");
        sql.append("'" + userid + "'");
        statement.executeUpdate(String.valueOf(sql));
    }
}
