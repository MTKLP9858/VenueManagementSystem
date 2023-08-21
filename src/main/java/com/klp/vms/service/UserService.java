package com.klp.vms.service;

import com.klp.vms.dao.UserDao;
import com.klp.vms.entity.User;
import com.klp.vms.exception.RuntimeError;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class UserService {
    public static long accessTokenAgeAdder = 12L * 60 * 60 * 1000;
    public static long refreshTokenAgeAdder = 30L * 24 * 60 * 60 * 1000;
    public static long autoRefreshAgeAdder = 3L * 24 * 60 * 60 * 1000;

    public static HashMap<String, String> doRefreshToken(String refresh_token) throws SQLException, RuntimeError {
        User user = verifyRefreshToken(refresh_token);
        String newAccessToken = updateAccessToken(user.getUserid());
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("access_token", newAccessToken);


        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date RefreshTokenAge = null;
        try {
            RefreshTokenAge = sdf.parse(user.getRefresh_token_age());
        } catch (ParseException e) {
            hashMap.put("refresh_token", updateRefreshToken(user.getUserid()));
        }
        if (RefreshTokenAge != null && RefreshTokenAge.before(new Date(new Date().getTime() + autoRefreshAgeAdder))) {
            hashMap.put("refresh_token", updateRefreshToken(user.getUserid()));
        }
        return hashMap;
    }

    public static User verifyAccessToken(String access_token) throws SQLException, RuntimeError {
        UserDao userDao = new UserDao();
        User user = userDao.execQueryBy("access_token", access_token);
        userDao.disConnect();
        if (user == null) {
            throw new RuntimeError("No such access_token", 111);
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date;
        try {
            date = sdf.parse(user.getAccess_token_age());
        } catch (ParseException e) {
            updateAccessToken(user.getAccess_token());
            throw new RuntimeError("access_token_age error,please update", 112);
        }
        if (date.after(new Date())) {
            return user;
        } else {
            updateAccessToken(user.getAccess_token());
            throw new RuntimeError("The access_token has expired", 113);
        }
    }


    public static String updateAccessToken(String userid) throws SQLException, RuntimeError {
        UserDao userDao = new UserDao();
        User user = userDao.execQuery(userid);
        userDao.disConnect();

        if (user == null) {
            throw new RuntimeError("No Such UserID!", 101);
        }

        String uuid = String.valueOf(UUID.randomUUID());

        userDao.connect();
        userDao.execUpdate("access_token", uuid, userid);
        userDao.disConnect();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sdfStr = sdf.format(new Date(new Date().getTime() + accessTokenAgeAdder));
        userDao.connect();
        userDao.execUpdate("access_token_age", sdfStr, userid);
        userDao.disConnect();
        return uuid;
    }

    public static User verifyRefreshToken(String refresh_token) throws SQLException, RuntimeError {
        UserDao userDao = new UserDao();
        User user = userDao.execQueryBy("refresh_token", refresh_token);
        userDao.disConnect();

        if (user == null) {
            throw new RuntimeError("No such refresh_token", 121);
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date;
        try {
            date = sdf.parse(user.getRefresh_token_age());
        } catch (ParseException e) {
            updateRefreshToken(user.getUserid());
            throw new RuntimeError("refresh_token_age error,please update", 122);
        }
        if (date.after(new Date())) {
            return user;
        } else {
            updateRefreshToken(user.getUserid());
            throw new RuntimeError("The refresh_token has expired", 123);
        }
    }

    public static String updateRefreshToken(String userid) throws SQLException, RuntimeError {
        UserDao userDao = new UserDao();
        User user = userDao.execQuery(userid);
        userDao.disConnect();

        if (user == null) {
            throw new RuntimeError("No Such UserID!", 101);
        }

        String uuid = String.valueOf(UUID.randomUUID());

        userDao.connect();
        userDao.execUpdate("refresh_token", uuid, userid);
        userDao.disConnect();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sdfStr = sdf.format(new Date(new Date().getTime() + refreshTokenAgeAdder));
        userDao.connect();
        userDao.execUpdate("refresh_token_age", sdfStr, userid);
        userDao.disConnect();
        return uuid;
    }

    public static User login(String userid, String password) throws SQLException, RuntimeError {
        UserDao userDao = new UserDao();
        User user = userDao.execQuery(userid);
        userDao.disConnect();

        if (user == null) {
            throw new RuntimeError("No Such UserID!", 101);
        }
        if (!Objects.equals(user.getPassword(), password)) {
            throw new RuntimeError("The password is incorrect!", 102);
        }

        if (user.getOp() == 5) {
            try {
                verifyAccessToken(user.getAccess_token());
                verifyRefreshToken(user.getRefresh_token());
            } catch (RuntimeError ignored) {
            }
        }

        if (user.getOp() == 0) {
            updateAccessToken(user.getUserid());
            updateRefreshToken(user.getUserid());
        }
        userDao.connect();
        user = userDao.execQuery(userid);
        userDao.disConnect();
        return user;
    }

    public static User register(String userid, String password, int op) throws SQLException, RuntimeError {
        return register(userid, null, password, op);
    }

    public static User register(String userid, String username, String password, int op) throws SQLException, RuntimeError {
        UserDao userDao = new UserDao();
        User userCheck = userDao.execQuery(userid);
        userDao.disConnect();
        if (userCheck != null) {
            throw new RuntimeError("This UserID already exists!", 107);
        }
        User user = new User(op);
        user.setUserid(userid);
        user.setUsername(username);
        user.setPassword(password);
        user.setAccess_token("register");
        user.setRefresh_token("register");

        userDao.connect();
        userDao.execInsert(user);
        userDao.disConnect();
        updateAccessToken(user.getUserid());
        updateRefreshToken(user.getUserid());

        userDao.connect();
        user = userDao.execQuery(userid);
        userDao.disConnect();
        return user;
    }

}
