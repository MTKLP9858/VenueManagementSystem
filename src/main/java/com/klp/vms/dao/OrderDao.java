package com.klp.vms.dao;

import com.klp.vms.entity.User;
import com.klp.vms.exception.RuntimeError;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class OrderDao implements Dao {
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
    public void execInsert(User user) throws SQLException {

    }

    @Override
    public void execDelete(String KeyColumn) throws SQLException {

    }

    @Override
    public List execQuery(String column, String value) throws SQLException {
        return null;
    }

    @Override
    public void execUpdate(String column, String value, String KeyColumn) throws SQLException {

    }
}
