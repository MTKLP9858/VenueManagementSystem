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

import static com.klp.vms.dao.ImageDao.imgPath;

public class UserDao implements Dao<User> {
    public File queryAvatar(String userid) throws RuntimeError {
        if (userid == null) return null;
        File file = new File(imgPath + userid + ".png");
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + defaultDataBaseUrl); Statement statement = connection.createStatement()) {
            ResultSet rs = statement.executeQuery("select avatar from User where userid ='" + userid + "';");
            if (rs.next()) {
                byte[] bytes = rs.getBytes("avatar");
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    if (bytes == null) return null;
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

    public boolean updateAvatar(String userid, File img) throws RuntimeError {
        String sql = "UPDATE User SET avatar=? where userid=?;";
        try (FileInputStream fis = new FileInputStream(img); Connection conn = DriverManager.getConnection("jdbc:sqlite:" + defaultDataBaseUrl); PreparedStatement stat = conn.prepareStatement(sql);) {
            //将指定的文件流对象放入连接对象中，进行封装性的执行
            stat.setBytes(1, fis.readAllBytes());
            stat.setString(2, userid);
            int r = stat.executeUpdate();
            return r > 0;
        } catch (IOException e) {
            throw new RuntimeError("FileInputStream error, file or path to file doesn't exists", 154);
        } catch (SQLException e) {
            throw new RuntimeError("Database error, check if the database path or data table exists", 11);
        }
    }


    @Override
    public ArrayList<User> execQuery(String TYPE, String value) throws SQLException, RuntimeError {
        if (value == null) return null;
        String sql = "select * from User where " + TYPE + "='" + value + "';";
        ArrayList<User> list;
        ResultSet rs = this.query(sql);
        list = new ArrayList<>();
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
        this.close();
        return list;
    }

    public User execQueryBy(String column, String value) throws SQLException, RuntimeError {
        if (value == null) return null;
        String sql = "select * from User where " + column + "='" + value + "';";
        User user = new User(-1);
        ResultSet rs = this.query(sql);
        if (rs.next()) {
            user.setUserid(rs.getString("userid"));
            user.setUsername(rs.getString("username"));
            user.setPassword(rs.getString("password"));
            user.setOp(rs.getInt("op"));
            user.setAccess_token(rs.getString("access_token"));
            user.setAccess_token_age(rs.getString("access_token_age"));
            user.setRefresh_token(rs.getString("refresh_token"));
            user.setRefresh_token_age(rs.getString("refresh_token_age"));
        }
        this.close();
        return user.getUserid() == null ? null : user;
    }

    public User execQuery(String userid) throws SQLException, RuntimeError {
        return execQueryBy("userid", userid);
    }

    public void execInsert(@NotNull User user) throws RuntimeError {
        StringBuilder sql = new StringBuilder("insert into User (userid, username, password, op, access_token, access_token_age, refresh_token, refresh_token_age) VALUES (");
        sql.append(user.getUserid() == null ? "NULL" : ("'" + user.getUserid() + "'")).append(",");
        sql.append(user.getUsername() == null ? "NULL" : ("'" + user.getUsername() + "'")).append(",");
        sql.append(user.getPassword() == null ? "NULL" : ("'" + user.getPassword() + "'")).append(",");
        sql.append(user.getOp()).append(",");
        sql.append(user.getAccess_token() == null ? "NULL" : ("'" + user.getAccess_token() + "'")).append(",");
        sql.append(user.getAccess_token_age() == null ? "NULL" : ("'" + user.getAccess_token_age() + "'")).append(",");
        sql.append(user.getRefresh_token() == null ? "NULL" : ("'" + user.getRefresh_token() + "'")).append(",");
        sql.append(user.getRefresh_token_age() == null ? "NULL" : ("'" + user.getRefresh_token_age() + "'"));
        sql.append(");");
        this.update(String.valueOf(sql));
    }

    public void execDelete(String userid) throws RuntimeError {
        this.update("delete FROM User where userid='" + userid + "';");
    }

    public void execUpdate(String column, String value, String userid) throws RuntimeError {
        StringBuilder sql = new StringBuilder("UPDATE User SET ");
        sql.append(column + "=");
        sql.append("'" + value + "'");
        sql.append(" WHERE userid=");
        sql.append("'" + userid + "'");
        this.update(String.valueOf(sql));
    }
}
