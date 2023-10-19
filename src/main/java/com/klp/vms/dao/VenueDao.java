package com.klp.vms.dao;

import com.alibaba.fastjson2.JSONArray;
import com.klp.vms.entity.Order;
import com.klp.vms.entity.Venue;
import com.klp.vms.exception.RuntimeError;
import com.klp.vms.service.ImageService;
import com.klp.vms.service.OrderService;
import org.springframework.web.multipart.MultipartFile;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class VenueDao implements Dao<Venue> {//场地

    public void swapIndex(int a, int b, String uuid) throws SQLException {
        JSONArray list = getImageList(uuid);
        String strA = list.getString(a);
        String strB = list.getString(b);
        list.set(b, strA);
        list.set(a, strB);
        setImageList(list, uuid);
    }

    public int getSizeOfImageList(String uuid) throws SQLException {
        return getImageList(uuid).size();
    }

    private JSONArray getImageList(String uuid) throws SQLException {
        try (Stat stat = new Stat("select image_list from Venue where uuid=?")) {
            stat.setString(1, uuid);
            String image_list = stat.executeQuery().getString("image_list");
            return JSONArray.parseArray(image_list == null ? "[]" : image_list);
        }
    }

    private int setImageList(JSONArray value, String uuid) throws SQLException {
        try (Stat stat = new Stat("update Venue set image_list=? where uuid=?;")) {
            stat.setString(1, value.toString());
            stat.setString(2, uuid);
            return stat.executeUpdate();
        }
    }

    public void imgInsert(int index, MultipartFile img, String uuid) throws SQLException, RuntimeError {
        JSONArray json = getImageList(uuid);
        if (index > json.size()) {
            throw new RuntimeError("Insert fail: The index you input is bigger then image_list's size", 270);
        }
        String img_index = ImageService.add(img);
        try {
            json.add(index, img_index);
            setImageList(json, uuid);
        } catch (SQLException e) {
            new ImageDao().execDelete(img_index);
            throw new SQLException(e);
        } catch (IndexOutOfBoundsException e) {
            new ImageDao().execDelete(img_index);
            throw new RuntimeError("IndexOutOfBoundsException: The index you input is bigger then image_list's size", 271);
        }
    }

    public boolean imgDelete(int index, String uuid) throws SQLException, RuntimeError {
        JSONArray json = getImageList(uuid);
        String img_index;
        try {
            img_index = json.getString(index);
        } catch (IndexOutOfBoundsException e) {
            throw new RuntimeError("IndexOutOfBoundsException: The index you input is bigger then image_list's size", 273);
        }
        //del from image_list_string
        json.remove(index);
        setImageList(json, uuid);
        //del from DataBase
        return ImageService.delete(img_index);
    }

    public byte[] imgQuery(int index, String uuid) throws SQLException, RuntimeError {
        JSONArray json = getImageList(uuid);
        String img_index;
        try {
            img_index = json.getString(index);
        } catch (IndexOutOfBoundsException e) {
            throw new RuntimeError("IndexOutOfBoundsException: The index you input is bigger then image_list's size", 272);
        }
        return ImageService.query(img_index);
    }

    public boolean imgUpdate(int index, MultipartFile img, String uuid) throws SQLException, RuntimeError {
        JSONArray json = getImageList(uuid);
        String img_index;
        try {
            img_index = json.getString(index);
        } catch (IndexOutOfBoundsException e) {
            throw new RuntimeError("IndexOutOfBoundsException: The index you input is bigger then image_list's size", 271);
        }
        return ImageService.update(img_index, img);
    }

    public String getUUID(String name, String area, String stadium) throws SQLException {
        String sql = "select uuid from Venue where name=? and area=? and stadium=?;";
        try (Stat stat = new Stat(sql)) {
            stat.setString(1, name);
            stat.setString(2, area);
            stat.setString(3, stadium);
            return stat.executeQuery().getString("uuid");
        }
    }

    @Override
    public int execInsert(Venue venue) throws RuntimeError, SQLException, ParseException {
        if (venue == null) return 0;
        if (new StadiumDao().execQuery("name", venue.getStadium()).isEmpty()) {
            throw new RuntimeError("no such value in Stadium.name!", 223);
        }

        String uuid = getUUID(venue.getName(), venue.getArea(), venue.getStadium());
        if (uuid != null) {
            throw new RuntimeError("The same venue exists!", 223);
        }
        String sql = "insert into Venue (uuid,name, area, stadium, introduction, state, price) VALUES (?,?,?,?,?,?,?);";
        try (Stat stat = new Stat(sql)) {
            stat.setString(1, venue.getUUID() == null ? String.valueOf(UUID.randomUUID()) : venue.getUUID());
            stat.setString(2, venue.getName());
            stat.setString(3, venue.getArea());
            stat.setString(4, venue.getStadium());
            stat.setString(5, venue.getIntroduction());
            stat.setString(6, venue.getState());
            stat.setDouble(7, venue.getPrice());
            return stat.executeUpdate();
        }
    }

    @Override
    public int execDelete(String uuid) throws SQLException {
        if (uuid != null) {
            String sql = "delete FROM Venue where uuid=?;";
            try (Stat stat = new Stat(sql)) {
                stat.setString(1, uuid);
                return stat.executeUpdate();
            }
        }
        return -1;
    }


    /**
     * @param area    if area is null then delete all venue which stadium match
     * @param stadium necessity
     */
    public int execDelete(String area, String stadium) throws SQLException {
        if (area != null && stadium != null) {
            String sql = "delete FROM Venue where area=? and stadium=?;";
            try (Stat stat = new Stat(sql)) {
                stat.setString(1, area);
                stat.setString(2, stadium);
                return stat.executeUpdate();
            }
        }
        if (area == null && stadium != null) {
            String sql = "delete FROM Venue where stadium=?;";
            try (Stat stat = new Stat(sql)) {
                stat.setString(1, stadium);
                return stat.executeUpdate();
            }
        }
        return -1;
    }

    public ArrayList<Venue> execQuery(long price) throws SQLException, ParseException, RuntimeError {
        return execQuery("price", String.valueOf(price));
    }

    public Venue execQuery(String uuid) throws SQLException, RuntimeError, ParseException {
        ArrayList<Venue> list = execQuery("uuid", uuid);
        if (list.size() != 1) {
            throw new RuntimeError("The target was not found or there are multiple identical targets!", 371);
        }
        return list.get(0);
    }

    @Override
    public ArrayList<Venue> execQuery(String column, Object value) throws SQLException, ParseException, RuntimeError {
        String sql = "select * from Venue where " + column + "=?;";
        ArrayList<Venue> list = new ArrayList<>();
        try (Stat stat = new Stat(sql)) {
            stat.setObject(1, value);
            ResultSet rs = stat.executeQuery();
            while (rs.next()) {
                Venue venue = new Venue();
                venue.setUUID(rs.getString("uuid"));
                venue.setName(rs.getString("name"));
                venue.setArea(rs.getString("area"));
                venue.setStadium(rs.getString("stadium"));
                venue.setIntroduction(rs.getString("introduction"));
                venue.setState(rs.getString("state"));
                venue.setPrice(rs.getDouble("price"));
                if (Objects.equals(venue.getState(), Venue.STATE.CLOSING)) {
                    if (OrderService.StateFilter(OrderService.queryOrderByVenueUUID(venue.getUUID()), new String[]{Order.STATE.PAID, Order.STATE.USING, Order.STATE.PAYING}).isEmpty()) {
                        venue.setState(Venue.STATE.CLOSED);
                        new VenueDao().execUpdate("state", Venue.STATE.CLOSED, venue.getUUID());
                    }
                }
                list.add(venue);
            }
        }
        return list;
    }

    public int execUpdate(String column, Object value, String uuid) throws RuntimeError, SQLException, ParseException {
        if (column == null || value == null || uuid == null) return 0;
        Venue venue = execQuery(uuid);
        if (venue == null) {
            throw new RuntimeError("Target not found!", 220);
        }
//      凭UUID查询是否存在该场馆
        if (new StadiumDao().execQuery("name", venue.getStadium()).isEmpty()) {
//          如果没有该场馆，证明该uuid对应的venue失效，执行删除
//            execDelete(null, venue.getStadium());
            throw new RuntimeError("error!", 219);
        }
        if (Objects.equals(column, "stadium")) {
            if (new StadiumDao().execQuery("name", (String) value).isEmpty()) {
                throw new RuntimeError("no such value in Stadium.name!", 223);
            }
        }
        if (Objects.equals(column, "name")) {
            if (getUUID((String) value, venue.getArea(), venue.getStadium()) != null) {
                throw new RuntimeError("The same Stadium.name exists!", 224);
            }
        }
        String sql = "UPDATE Venue SET " + column + "=? WHERE uuid=?;";
        try (Stat stat = new Stat(sql)) {
            stat.setString(1, column);
            stat.setObject(2, value);
            stat.setString(3, uuid);
            return stat.executeUpdate();
        }
    }
}
