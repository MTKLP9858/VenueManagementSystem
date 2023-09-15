package com.klp.vms.dao;

import com.alibaba.fastjson2.JSONArray;
import com.klp.vms.entity.Stadium;
import com.klp.vms.entity.Venue;
import com.klp.vms.exception.RuntimeError;
import com.klp.vms.service.ImageService;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StadiumDao implements Dao<Stadium> {

    public void swapIndex(int a, int b, String name) throws SQLException {
        JSONArray list = getImageList(name);
        String strA = list.getString(a);
        String strB = list.getString(b);
        list.set(b, strA);
        list.set(a, strB);
        setImageList(list, name);
    }

    public int getSizeOfImageList(String name) throws SQLException {
        return getImageList(name).size();
    }

    private JSONArray getImageList(String name) throws SQLException {
        try (Stat stat = new Stat("select image_list from Stadium where name=?")) {
            stat.setString(1, name);
            String image_list = stat.executeQuery().getString("image_list");
            return JSONArray.parseArray(image_list == null ? "[]" : image_list);
        }
    }

    private int setImageList(JSONArray value, String name) throws SQLException {
        try (Stat stat = new Stat("update Stadium set image_list=? where name=?;")) {
            stat.setString(1, value.toString());
            stat.setString(2, name);
            return stat.executeUpdate();
        }
    }

    public void imgInsert(int index, MultipartFile img, String name) throws SQLException, RuntimeError {
        JSONArray json = this.getImageList(name);
        if (index > json.size()) {
            throw new RuntimeError("Insert fail: The index you input is bigger then image_list's size", 270);
        }
        String img_index = ImageService.add(img);
        try {
            json.add(index, img_index);
            setImageList(json, name);
        } catch (SQLException e) {
            new ImageDao().execDelete(img_index);
            throw new SQLException(e);
        } catch (IndexOutOfBoundsException e) {
            new ImageDao().execDelete(img_index);
            throw new RuntimeError("IndexOutOfBoundsException: The index you input is bigger then image_list's size", 271);
        }
    }

    public boolean imgDelete(int index, String name) throws SQLException, RuntimeError {
        JSONArray json = getImageList(name);
        String img_index;
        try {
            img_index = json.getString(index);
        } catch (IndexOutOfBoundsException e) {
            throw new RuntimeError("IndexOutOfBoundsException: The index you input is bigger then image_list's size", 273);
        }
        //del from image_list_string
        json.remove(index);
        setImageList(json, name);
        //del from DataBase
        return ImageService.delete(img_index);
    }

    public byte[] imgQuery(int index, String name) throws SQLException, RuntimeError {
        JSONArray json = getImageList(name);
        String img_index;
        try {
            img_index = json.getString(index);
        } catch (IndexOutOfBoundsException e) {
            throw new RuntimeError("IndexOutOfBoundsException: The index you input is bigger then image_list's size", 272);
        }
        return ImageService.query(img_index);
    }

    public boolean imgUpdate(int index, MultipartFile img, String name) throws SQLException, RuntimeError {
        JSONArray json = getImageList(name);
        String img_index;
        try {
            img_index = json.getString(index);
        } catch (IndexOutOfBoundsException e) {
            throw new RuntimeError("IndexOutOfBoundsException: The index you input is bigger then image_list's size", 271);
        }
        return ImageService.update(img_index, img);
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
    public int execUpdate(String column, Object value, String name) throws SQLException {
        String sql = "UPDATE Stadium SET " + column + "=? WHERE name=?";
        try (Stat stat = new Stat(sql)) {
            stat.setObject(1, value);
            stat.setString(2, name);
            return stat.executeUpdate();
        }
    }
}
