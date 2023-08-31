package com.klp.vms.dao;

import com.klp.vms.exception.RuntimeError;

import java.sql.*;
import java.util.HashMap;
import java.util.List;

public interface Dao<T> {
    String defaultDataBaseUrl = "main.db";
    HashMap<String, Object> map = new HashMap<>();

    default ResultSet query(String sql) throws RuntimeError {
        try {
            Class.forName("org.sqlite.JDBC");
            map.put("conn", DriverManager.getConnection("jdbc:sqlite:" + defaultDataBaseUrl));
            map.put("stat", ((Connection) map.get("conn")).createStatement());
            return ((Statement) map.get("stat")).executeQuery(sql);
        } catch (SQLException e) {
            throw new RuntimeError("Database error, check if the database path or data table exists", 11);
        } catch (ClassNotFoundException e) {
            throw new RuntimeError("Missing org.sqlite.JDBC, please check the server environment!", 10);
        }
    }

    default void close() {
        try {
            ((Statement) map.get("stat")).close();
            ((Connection) map.get("conn")).close();
        } catch (SQLException ignored) {
        }
    }

    default void update(String sql) throws RuntimeError {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeError("Missing org.sqlite.JDBC, please check the server environment!", 10);
        }
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + defaultDataBaseUrl); Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeError("Database error, check if the database path or data table exists: " + e.getMessage(), 11);
        }
    }

    void execInsert(T insertValue) throws Exception;

    void execDelete(String KeyColumn) throws Exception;

    List<T> execQuery(String column, String value) throws Exception;

    void execUpdate(String column, String value, String KeyColumn) throws Exception;
}
