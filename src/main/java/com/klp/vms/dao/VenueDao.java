package com.klp.vms.dao;

import com.alibaba.fastjson2.JSONArray;
import com.klp.vms.entity.Venue;
import com.klp.vms.exception.RuntimeError;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;

public class VenueDao implements Dao<Venue> {//场地

    private String getImageList(String name, String stadium) throws SQLException {
        try (Stat stat = new Stat("select image_list from Venue where name=? and stadium=?;")) {
            stat.setString(1, name);
            stat.setString(2, stadium);
            return stat.executeQuery().getString("image_list");
        }
    }

    private int setImageList(String value, String name, String stadium) throws SQLException {
        try (Stat stat = new Stat("update Venue set image_list=? where name=? and stadium=?;")) {
            stat.setString(1, value);
            stat.setString(2, name);
            stat.setString(3, stadium);
            return stat.executeUpdate();
        }
    }

    public void imgInsert(int index, File img, String name, String stadium) throws SQLException, RuntimeError {
        String image_list = getImageList(name, stadium);
        JSONArray json = JSONArray.parseArray(image_list == null ? "[]" : image_list);
        if (index > json.size()) {
            throw new RuntimeError("Insert fail:  The index you input is bigger then image_list's size", 270);
        }
        String img_index = new ImageDao().execInsert(img);
        try {
            json.add(index, img_index);
            setImageList(json.toString(), name, stadium);
        } catch (SQLException e) {
            new ImageDao().execDelete(img_index);
            throw new SQLException(e);
        } catch (IndexOutOfBoundsException e) {
            new ImageDao().execDelete(img_index);
            throw new RuntimeError("IndexOutOfBoundsException: The index you input is bigger then image_list's size", 271);
        }
    }

    public boolean imgDelete(int index, String name, String stadium) throws SQLException, RuntimeError {
        String image_list = getImageList(name, stadium);
        JSONArray json = JSONArray.parseArray(image_list == null ? "[]" : image_list);
        String img_index = json.getString(index);
        //del from image_list_string
        json.remove(index);
        setImageList(json.toString(), name, stadium);
        //del from DataBase
        return new ImageDao().execDelete(img_index);
    }

    public File imgQuery(int index, String name, String stadium) throws SQLException, RuntimeError {
        String image_list = getImageList(name, stadium);
        JSONArray json = JSONArray.parseArray(image_list == null ? "[]" : image_list);
        String img_index = json.getString(index);
        return new ImageDao().execQuery(img_index);
    }

    public void imgUpdate(int index, File img, String name, String stadium) throws SQLException, RuntimeError {
        String image_list = getImageList(name, stadium);
        JSONArray json = JSONArray.parseArray(image_list == null ? "[]" : image_list);
        String img_index = json.getString(index);
        new ImageDao().execUpdate(img_index, img);
    }


    @Override
    public int execInsert(Venue venue) throws RuntimeError, SQLException {
        if (venue == null) return 0;
        if (new StadiumDao().execQuery("name", venue.getStadium()).isEmpty()) {
            throw new RuntimeError("no such value in Stadium.name!", 223);
        }
        if (execQueryBy(venue.getName(), venue.getStadium()) != null) {
            throw new RuntimeError("The same Stadium.name exists!", 223);
        }
        String sql = "insert into Venue (name, area, stadium, introduction, active, price) VALUES (?,?,?,?,?,?);";
        try (Stat stat = new Stat(sql)) {
            stat.setString(1, venue.getName());
            stat.setString(2, venue.getArea());
            stat.setString(3, venue.getStadium());
            stat.setString(4, venue.getIntroduction());
            stat.setBoolean(5, venue.isActive());
            stat.setDouble(6, venue.getPrice());
            return stat.executeUpdate();
        }
    }

    /**
     * @param stadium Delete all the Venue which stadium eq this param!
     * @return
     */
    @Override
    public int execDelete(String stadium) throws SQLException {
        if (stadium != null) {
            String sql = "delete FROM Venue where stadium=?;";
            try (Stat stat = new Stat(sql)) {
                stat.setString(1, stadium);
                return stat.executeUpdate();
            }
        }
        return -1;
    }

    public int execDelete(String name, String stadium) throws SQLException {
        if (name != null && stadium != null) {
            String sql = "delete FROM Venue where name=? and stadium=?;";
            try (Stat stat = new Stat(sql)) {
                stat.setString(1, name);
                stat.setString(2, stadium);
                return stat.executeUpdate();
            }
        }
        return -1;
    }

    public ArrayList<Venue> execQuery(long price) throws SQLException, RuntimeError {
        return execQuery("price", String.valueOf(price));
    }

    public ArrayList<Venue> execQuery(boolean isActive) throws SQLException, RuntimeError {
        return execQuery("active", isActive ? "1" : "0");
    }

    public Venue execQueryBy(String name, String stadium) throws SQLException, RuntimeError {
        if (stadium == null) return null;
        ArrayList<Venue> listOfName = execQuery("name", name);
        if (listOfName == null) return null;
        ArrayList<Venue> list = new ArrayList<>();
        for (Venue v : listOfName) {
            if (Objects.equals(v.getStadium(), stadium)) {
                list.add(v);
            }
        }
        if (list.size() == 1) {
            return list.get(0);
        } else if (list.isEmpty()) {
            return null;
        } else {
            throw new RuntimeError("There are more than one Venue have the same name and stadium!", 222);
        }
    }

    @Override
    public ArrayList<Venue> execQuery(String column, String value) throws SQLException, RuntimeError {
        if (value == null) return null;
        String sql = "select * from Venue where " + column + "=?;";
        ArrayList<Venue> list = new ArrayList<>();
        try (Stat stat = new Stat(sql)) {
            stat.setString(1, value);
            ResultSet rs = stat.executeQuery();
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
        }
        return list;
    }

    /**
     * @return
     * @deprecated
     */
    @Deprecated
    public int execUpdate(String column, String value, String KEY) {
        return -1;
    }

    public int execUpdate(String column, String value, String name, String stadium) throws RuntimeError, SQLException {
        if (column == null || value == null || name == null || stadium == null) return 0;
        if (execQueryBy(name, stadium) == null) {
            throw new RuntimeError("Target not found!", 220);
        }
        if (new StadiumDao().execQuery("name", stadium).isEmpty()) {
            throw new RuntimeError("no such value in Stadium.name!", 223);
        }
        if (Objects.equals(column, "stadium")) {
            if (new StadiumDao().execQuery("name", value).isEmpty()) {
                throw new RuntimeError("no such value in Stadium.name!", 223);
            }
        }
        if (Objects.equals(column, "name")) {
            if (execQueryBy(value, stadium) != null) {
                throw new RuntimeError("The same Stadium.name exists!", 224);
            }
        }
        String sql = "UPDATE Venue SET " + column + "=? WHERE name=? and stadium=?;";
        try (Stat stat = new Stat(sql)) {
            stat.setString(1, column);
            stat.setString(2, value);
            stat.setString(3, name);
            stat.setString(4, stadium);
            return stat.executeUpdate();
        }
    }
}
