package com.klp.vms.dao;

import com.klp.vms.entity.Venue;
import com.klp.vms.exception.RuntimeError;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;

public class VenueDao implements Dao<Venue> {//场地

    Statement statement;
    Connection connection;

    VenueDao() throws RuntimeError {
        connect();
    }

    @Override
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
    public void disConnect() {
        try {
            statement.close();
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void execInsert(@NotNull Venue venue) throws SQLException {
        StringBuilder sql = new StringBuilder("insert into Venue (name, area, stadium, introduction, active, price) VALUES (");
        sql.append(venue.getName() == null ? "NULL" : ("'" + venue.getName() + "'")).append(",");
        sql.append(venue.getArea() == null ? "NULL" : ("'" + venue.getArea() + "'")).append(",");
        sql.append(venue.getStadium() == null ? "NULL" : ("'" + venue.getStadium() + "'")).append(",");
        sql.append(venue.getIntroduction() == null ? "NULL" : ("'" + venue.getIntroduction() + "'")).append(",");
        sql.append(venue.isActive()).append(",");
        sql.append(venue.getPrice());
        sql.append(");");
        statement.executeUpdate(String.valueOf(sql));
    }

    @Override
    public void execDelete(String name) throws SQLException {
        statement.executeUpdate("delete FROM Stadium where name='" + name + "';");
    }

    public ArrayList<Venue> execQuery(long price) throws SQLException {
        return execQuery("price", String.valueOf(price));
    }

    public ArrayList<Venue> execQuery(boolean isActive) throws SQLException {
        return execQuery("active", isActive ? "1" : "0");
    }

    @Override
    public ArrayList<Venue> execQuery(String column, String value) throws SQLException {
        if (value == null) return null;
        String sql = "select * from Venue where " + column + "='" + value + "';";
        ResultSet rs = statement.executeQuery(sql);
        ArrayList<Venue> list = new ArrayList<>();
        while (rs.next()) {
            Venue venue = new Venue();
            venue.setName(rs.getString("name"));
            venue.setArea(rs.getString("area"));
            venue.setStadium(rs.getString("stadium"));
            venue.setIntroduction(rs.getString("introduction"));
            venue.setActive(rs.getBoolean("active"));
            venue.setPrice(rs.getDouble("price"));
            list.add(venue);
        }
        return list;
    }

    @Override
    public void execUpdate(String column, String value, String name) throws SQLException {
        String sql = "UPDATE Venue SET " + column + "='" + value + "'" + " WHERE name='" + name + "'";
        statement.executeUpdate(sql);
    }


    public void execUpdate(String column, Boolean isActive, String name) throws SQLException {
        if (Objects.equals(column, "active")) {
            if (isActive) {
                execUpdate("active", "1", name);
            } else {
                execUpdate("active", "0", name);
            }
        }
    }

    public void execUpdate(String column, Double price, String name) throws SQLException {
        if (Objects.equals(column, "price")) {
            execUpdate("price", String.valueOf(price), name);
        }
    }
}
