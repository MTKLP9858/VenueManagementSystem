package com.klp.vms.dao;

import com.klp.vms.exception.RuntimeError;

import java.sql.*;
import java.util.List;

public interface Dao<T> {
    String defaultDataBaseUrl = "main.db";

    default ResultSet query(String sql) throws RuntimeError {
        try {
            Class.forName("org.sqlite.JDBC");
            Connection connection = DriverManager.getConnection("jdbc:sqlite:" + defaultDataBaseUrl);
            Statement statement = connection.createStatement();
            return statement.executeQuery(sql);
        } catch (SQLException e) {
            throw new RuntimeError("Database error, check if the database path or data table exists", 11);
        } catch (ClassNotFoundException e) {
            throw new RuntimeError("Missing org.sqlite.JDBC, please check the server environment!", 10);
        }
    }

    default void update(String sql) throws RuntimeError {
        try {
            Class.forName("org.sqlite.JDBC");
            Connection connection = DriverManager.getConnection("jdbc:sqlite:" + defaultDataBaseUrl);
            Statement statement = connection.createStatement();
            statement.executeUpdate(sql);
            statement.close();
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeError("Database error, check if the database path or data table exists", 11);
        } catch (ClassNotFoundException e) {
            throw new RuntimeError("Missing org.sqlite.JDBC, please check the server environment!", 10);
        }
    }

    void execInsert(T insertValue) throws Exception;

    void execDelete(String KeyColumn) throws Exception;

    List<T> execQuery(String column, String value) throws Exception;

    void execUpdate(String column, String value, String KeyColumn) throws Exception;
}
