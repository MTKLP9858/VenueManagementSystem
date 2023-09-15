package com.klp.vms.dao;

import com.klp.vms.entity.User;
import com.klp.vms.exception.RuntimeError;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;

import static com.klp.vms.dao.ImageDao.ImgTempPath;

public class UserDao implements Dao<User> {
    public File queryAvatar(String userid) throws RuntimeError {
        String sql = "select avatar from User where userid=?;";
        if (!new File(ImgTempPath).exists()) new File(ImgTempPath).mkdirs();
        if (userid == null) return null;
        File file = new File(ImgTempPath + userid + ".png");
        try (Stat stat = new Stat(sql)) {
            stat.setString(1, userid);
            ResultSet rs = stat.executeQuery();
            if (rs.next()) {
                byte[] bytes = rs.getBytes("avatar");
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    if (bytes == null) throw new RuntimeError("avatar not found", 157);
                    fos.write(bytes);
                }
            }
        } catch (IOException e) {
            throw new RuntimeError("FileInputStream error, file or path to file doesn't exists", 154);
        } catch (SQLException e) {
            throw new RuntimeError("Database error, check if the database path or data table exists", 11);
        }
        return file;
    }

    public boolean updateAvatar(String userid, File img) throws RuntimeError, SQLException {
        String sql = "UPDATE User SET avatar=? where userid=?;";
        if (userid == null) return false;
        if (img == null) {
            execUpdate("avatar", null, userid);
            return true;
        }
        if (!new File(ImgTempPath).exists()) new File(ImgTempPath).mkdirs();
        try (FileInputStream fis = new FileInputStream(img); Stat stat = new Stat(sql);) {
            stat.setBytes(1, fis.readAllBytes());
            stat.setString(2, userid);
            int r = stat.executeUpdate();
            return r > 0;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeError("FileInputStream error, file or path to file doesn't exists: " + e.getMessage(), 154);
        } catch (SQLException e) {
            throw new RuntimeError("Database error, check if the database path or data table exists", 11);
        }
    }


    @Override
    public ArrayList<User> execQuery(String column, Object value) throws SQLException {
        String sql = "select * from User where " + column + "=?;";
        ArrayList<User> list = new ArrayList<>();
        try (Stat stat = new Stat(sql)) {
            stat.setObject(1, value);
            ResultSet rs = stat.executeQuery();
            while (rs.next()) {
                User user = new User(-1);
                user.setUserid(rs.getString("userid"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setOp(rs.getInt("op"));
                user.setAccess_token(rs.getString("access_token"));
                user.setAccess_token_age(rs.getString("access_token_age"));
                user.setRefresh_token(rs.getString("refresh_token"));
                user.setRefresh_token_age(rs.getString("refresh_token_age"));
                list.add(user);
            }
        }
        return list;
    }

    public User execQuery(String userid) throws SQLException {
        ArrayList<User> list = execQuery("userid", userid);
        return list.isEmpty() ? null : list.get(0);
    }

    public int execInsert(@NotNull User user) throws SQLException {
        String sql = "insert into User (userid, username, password, op, access_token, access_token_age, refresh_token, refresh_token_age) VALUES (?,?,?,?,?,?,?,?);";
        try (Stat stat = new Stat(sql)) {
            stat.setString(1, user.getUserid());
            stat.setString(2, user.getUsername());
            stat.setString(3, user.getPassword());
            stat.setInt(4, user.getOp());
            stat.setString(5, user.getAccess_token());
            stat.setString(6, user.getAccess_token_age());
            stat.setString(7, user.getRefresh_token());
            stat.setString(8, user.getRefresh_token_age());
            return stat.executeUpdate();
        }
    }

    public int execDelete(String userid) throws SQLException {
        try (Stat stat = new Stat("delete FROM User where userid=?;").setString(1, userid)) {
            return stat.executeUpdate();
        }
    }

    public int execUpdate(String column, Object value, String userid) throws SQLException {
        String sql = "UPDATE User SET " + column + "=? WHERE userid=?;";
        try (Stat stat = new Stat(sql)) {
            stat.setObject(1, value);
            stat.setString(2, userid);
            return stat.executeUpdate();
        }
    }
}
