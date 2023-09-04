package com.klp.vms.dao;

import com.alibaba.fastjson2.JSONArray;
import com.klp.vms.entity.Stadium;
import com.klp.vms.entity.Venue;
import com.klp.vms.exception.RuntimeError;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StadiumDao implements Dao<Stadium> {
    private String getImageList(String name) throws SQLException {
        try (Stat stat = new Stat("select image_list from Stadium where name=?;")) {
            stat.setString(1, name);
            return stat.executeQuery().getString("image_list");
        }
    }

    private int setImageList(String value, String name) throws SQLException {
        try (Stat stat = new Stat("update Stadium set image_list=? where name=?;")) {
            stat.setString(1, value);
            stat.setString(2, name);
            return stat.executeUpdate();
        }
    }

    public void imgInsert(int index, File img, String name) throws SQLException, RuntimeError {
        String image_list = getImageList(name);
        JSONArray json = JSONArray.parseArray(image_list == null ? "[]" : image_list);
        if (index > json.size()) {
            throw new RuntimeError("Insert fail:  The index you input is bigger then image_list's size", 270);
        }
        String img_index = new ImageDao().execInsert(img);
        try {
            json.add(index, img_index);
            setImageList(json.toString(), name);
        } catch (SQLException e) {
            new ImageDao().execDelete(img_index);
            throw new SQLException(e);
        } catch (IndexOutOfBoundsException e) {
            new ImageDao().execDelete(img_index);
            throw new RuntimeError("IndexOutOfBoundsException: The index you input is bigger then image_list's size", 271);
        }
    }

    public boolean imgDelete(int index, String name) throws SQLException, RuntimeError {
        String image_list = getImageList(name);
        JSONArray json = JSONArray.parseArray(image_list == null ? "[]" : image_list);
        String img_index = json.getString(index);
        //del from image_list_string
        json.remove(index);
        setImageList(json.toString(), name);
        //del from DataBase
        return new ImageDao().execDelete(img_index);
    }

    public File imgQuery(int index, String name) throws SQLException, RuntimeError {
        String image_list = getImageList(name);
        JSONArray json = JSONArray.parseArray(image_list == null ? "[]" : image_list);
        String img_index = json.getString(index);
        return new ImageDao().execQuery(img_index);
    }

    public void imgUpdate(int index, File img, String name) throws SQLException, RuntimeError {
        String image_list = getImageList(name);
        JSONArray json = JSONArray.parseArray(image_list == null ? "[]" : image_list);
        String img_index = json.getString(index);
        new ImageDao().execUpdate(img_index, img);
    }


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
        String sql = "select * from Stadium where " + column + "=?;";
        ArrayList<Stadium> list = new ArrayList<>();
        try (Stat stat = new Stat(sql)) {
            stat.setString(1, value);
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
        String sql = "UPDATE Stadium SET " + column + "=? WHERE name=?";
        try (Stat stat = new Stat(sql)) {
            stat.setString(1, value);
            stat.setString(2, name);
            return stat.executeUpdate();
        }
    }
}
