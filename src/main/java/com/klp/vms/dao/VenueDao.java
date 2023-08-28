package com.klp.vms.dao;

import com.klp.vms.entity.Venue;
import com.klp.vms.exception.RuntimeError;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.ArrayList;

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
        sql.append(venue.getName() == null ? "NULL" : ("'" + venue.getName()) + "'").append(",");
        sql.append(venue.getArea() == null ? "NULL" : ("'" + venue.getArea()) + "'").append(",");
        sql.append(venue.getStadium() == null ? "NULL" : ("'" + venue.getStadium()) + "'").append(",");
        sql.append(venue.getIntroduction() == null ? "NULL" : ("'" + venue.getIntroduction()) + "'").append(",");
        sql.append(venue.isActive()).append(",");
        sql.append(venue.getPrice());
        sql.append(");");
        statement.executeUpdate(String.valueOf(sql));
    }

    @Override
    public void execDelete(String name) throws SQLException {
        statement.executeUpdate("delete FROM Stadium where name='" + name + "';");
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
        StringBuilder sql = new StringBuilder("UPDATE Venue SET ");
        sql.append(column + "=");
        sql.append("'" + value + "'");
        sql.append(" WHERE name=");
        sql.append("'" + name + "'");
        statement.executeUpdate(String.valueOf(sql));
    }
}
