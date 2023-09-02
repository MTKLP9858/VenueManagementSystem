package com.klp.vms.dao;

import com.klp.vms.entity.Stadium;
import com.klp.vms.entity.Venue;
import com.klp.vms.exception.RuntimeError;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StadiumDao implements Dao<Stadium> {
    @Override
    public int execInsert(@NotNull Stadium stadium) throws SQLException {
        String sql = "insert into Stadium (name, address, introduction, contact, adminUserID) VALUES (?,?,?,?,?);";
        try (Stat stat = new Stat(sql)) {
            stat.setString(1, stadium.getName());
            stat.setString(2, stadium.getAddress());
            stat.setString(3, stadium.getIntroduction());
            stat.setString(4, stadium.getContact());
            stat.setString(5, stadium.getAdminUserID());
            return stat.executeUpdate();
        }
    }

    @Override
    public int execDelete(String name) throws SQLException {
        try (Stat stat = new Stat("delete FROM Stadium where name=?;")) {
            stat.setString(1, name);
            return stat.executeUpdate();
        }
    }

    @Override
    public List<Stadium> execQuery(String column, String value) throws SQLException, RuntimeError {
        if (value == null) return null;
        String sql = "select * from Stadium where ?=?;";
        ArrayList<Stadium> list = new ArrayList<>();
        try (Stat stat = new Stat(sql)) {
            stat.setString(1, column);
            stat.setString(2, value);
            ResultSet rs = stat.executeQuery();
            while (rs.next()) {
                Stadium stadium = new Stadium();
                stadium.setName(rs.getString("name"));
                stadium.setAddress(rs.getString("address"));
                stadium.setIntroduction(rs.getString("introduction"));
                stadium.setContact(rs.getString("contact"));
                stadium.setAdminUserID(rs.getString("adminUserID"));
                stadium.setAdminUser(new UserDao().execQuery(stadium.getAdminUserID()));
                ArrayList<Venue> venueList = new VenueDao().execQuery("stadium", stadium.getName());
                stadium.setVenues(venueList);
                list.add(stadium);
            }
        }
        return list;
    }

    @Override
    public int execUpdate(String column, String value, String name) throws SQLException {
        String sql = "UPDATE Stadium SET ?=? WHERE name=?";
        try (Stat stat = new Stat(sql)) {
            stat.setString(1, column);
            stat.setString(2, value);
            stat.setString(3, name);
            return stat.executeUpdate();
        }
    }
}
