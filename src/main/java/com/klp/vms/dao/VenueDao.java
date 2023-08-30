package com.klp.vms.dao;

import com.klp.vms.entity.Venue;
import com.klp.vms.exception.RuntimeError;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;

public class VenueDao implements Dao<Venue> {//场地

    @Override
    public void execInsert(@NotNull Venue venue) throws RuntimeError {
        StringBuilder sql = new StringBuilder("insert into Venue (name, area, stadium, introduction, active, price) VALUES (");
        sql.append(venue.getName() == null ? "NULL" : ("'" + venue.getName().replaceAll("'", "''") + "'")).append(",");
        sql.append(venue.getArea() == null ? "NULL" : ("'" + venue.getArea().replaceAll("'", "''") + "'")).append(",");
        sql.append(venue.getStadium() == null ? "NULL" : ("'" + venue.getStadium().replaceAll("'", "''") + "'")).append(",");
        sql.append(venue.getIntroduction() == null ? "NULL" : ("'" + venue.getIntroduction().replaceAll("'", "''") + "'")).append(",");
        sql.append(venue.isActive()).append(",");
        sql.append(venue.getPrice());
        sql.append(");");
        this.update(String.valueOf(sql));
    }

    @Override
    public void execDelete(String name) throws RuntimeError {
        this.update("delete FROM Stadium where name='" + name.replaceAll("'", "''") + "';");
    }

    public ArrayList<Venue> execQuery(long price) throws SQLException, RuntimeError {
        return execQuery("price", String.valueOf(price));
    }

    public ArrayList<Venue> execQuery(boolean isActive) throws SQLException, RuntimeError {
        return execQuery("active", isActive ? "1" : "0");
    }

    @Override
    public ArrayList<Venue> execQuery(String column, String value) throws SQLException, RuntimeError {
        if (value == null) return null;
        String sql = "select * from Venue where " + column.replaceAll("'", "''") + "='" + value.replaceAll("'", "''") + "';";
        ArrayList<Venue> list = new ArrayList<>();
        ResultSet rs = this.query(sql);
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
        this.close();
        return list;
    }

    @Override
    public void execUpdate(String column, String value, String name) throws RuntimeError {
        String sql = "UPDATE Venue SET " + column.replaceAll("'", "''") + "='" + value.replaceAll("'", "''") + "'" + " WHERE name='" + name.replaceAll("'", "''") + "'";
        this.update(sql);
    }


    public void execUpdate(String column, boolean isActive, String name) throws RuntimeError {
        if (Objects.equals(column, "active")) {
            if (isActive) {
                execUpdate("active", "1", name);
            } else {
                execUpdate("active", "0", name);
            }
        }
    }

    public void execUpdate(String column, Double price, String name) throws RuntimeError {
        if (Objects.equals(column, "price")) {
            execUpdate("price", String.valueOf(price), name);
        }
    }
}
