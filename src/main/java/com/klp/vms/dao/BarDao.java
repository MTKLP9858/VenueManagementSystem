package com.klp.vms.dao;

import com.alibaba.fastjson.JSONObject;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BarDao {
    public int execInsert(String message) throws SQLException {
        int length = 0;
        try (Stat stat = new Stat("select count(*) len from bar")) {
            ResultSet rs = stat.executeQuery();
            if (rs.next()) {
                length = rs.getInt("len");
            }
        }
        String sql = "INSERT INTO bar(idx,msg) VALUES(?,?)";
        try (Stat stat = new Stat(sql)) {
            stat.setInt(1, length);
            stat.setObject(2, message);
            return stat.executeUpdate();
        }
    }

    public int execDelete(int index) throws SQLException {
        try (Stat stat = new Stat("DELETE FROM bar WHERE idx=?;")) {
            stat.setInt(1, index);
            return stat.executeUpdate();
        }
    }

    public JSONObject execQuery() throws SQLException {
        JSONObject json = new JSONObject();
        int length = 0;
        try (Stat stat = new Stat("SELECT * from bar")) {
            ResultSet rs = stat.executeQuery();
            while (rs.next()) {
                int index = rs.getInt("idx");
                String message = rs.getString("msg");
                json.put(String.valueOf(index), message);
                length++;
            }
        }
        json.put("length", length);
        return json;
    }

    public int execUpdate(int index, String message) throws SQLException {
        try (Stat stat = new Stat("UPDATE bar SET msg=? WHERE idx=?;")) {
            stat.setObject(1, message);
            stat.setInt(2, index);
            return stat.executeUpdate();
        }
    }
}
