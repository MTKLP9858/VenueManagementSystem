package com.klp.vms.service;

import com.klp.vms.dao.ImageDao;
import com.klp.vms.dao.UserDao;
import com.klp.vms.entity.User;
import com.klp.vms.exception.RuntimeError;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class UserService {
    public static long accessTokenAgeAdder = 12L * 60 * 60 * 1000;//12h
    public static long refreshTokenAgeAdder = 30L * 24 * 60 * 60 * 1000;
    public static long autoRefreshAgeAdder = 3L * 24 * 60 * 60 * 1000;


    public static boolean updateAvatar(String access_token, @NotNull MultipartFile img) throws RuntimeError, SQLException {
        ImageDao.clearOutDateCache();
        File file = new File(ImageDao.ImgTempPath + img.getOriginalFilename());
        try {
            User user = verifyAccessToken(access_token);
            img.transferTo(file);
            if (!ImageService.isImage(file)) throw new RuntimeError("avatar image broken, please upload again", 155);
            return new UserDao().updateAvatar(user.getUserid(), file);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeError(e.getMessage(), 151);
        }
    }

    public static byte[] queryAvatar(String access_token) throws RuntimeError, SQLException {
        ImageDao.clearOutDateCache();
        UserDao userDao = new UserDao();
        User user = verifyAccessToken(access_token);
        File file = userDao.queryAvatar(user.getUserid());
        if (file == null) throw new RuntimeError("avatar not found", 156);
        if (!ImageService.isImage(file)) throw new RuntimeError("avatar image broken, please upload again", 155);
        try {
            return Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            throw new RuntimeError(e.getMessage(), 151);
        }
    }

    public static void rename(String newUsername, String access_token) throws SQLException, RuntimeError {
        if (newUsername == null || Objects.equals(newUsername.trim(), "")) {
            throw new RuntimeError("The name entered is empty", 121);
        }
        User user = verifyAccessToken(access_token);
        if (Objects.equals(user.getUsername(), newUsername.trim())) {
            throw new RuntimeError("The new name duplicates the old name!", 120);
        }
        new UserDao().execUpdate("username", newUsername.trim(), user.getUserid());
    }

    public static @NotNull HashMap<String, String> doRefreshToken(String refresh_token) throws SQLException, RuntimeError {
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
        ArrayList<User> listOfUser = new UserDao().execQuery("access_token", access_token);
        if (listOfUser.isEmpty()) {
            throw new RuntimeError("No such access_token", 111);
        }
        User user = listOfUser.get(0);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date;
        try {
            date = sdf.parse(user.getAccess_token_age());
        } catch (ParseException e) {
            updateAccessToken(user.getUserid());
            throw new RuntimeError("access_token_age error,please update", 112);
        }
        if (date.after(new Date())) {
            return user;
        } else {
            updateAccessToken(user.getUserid());
            throw new RuntimeError("The access_token has expired", 113);
        }
    }


    public static String updateAccessToken(String userid) throws SQLException, RuntimeError {
        User user = new UserDao().execQuery(userid);
        if (user == null) {
            throw new RuntimeError("No Such UserID!", 101);
        }

        String uuid = String.valueOf(UUID.randomUUID());

        new UserDao().execUpdate("access_token", uuid, userid);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sdfStr = sdf.format(new Date(new Date().getTime() + accessTokenAgeAdder));
        new UserDao().execUpdate("access_token_age", sdfStr, userid);
        return uuid;
    }

    public static @NotNull User verifyRefreshToken(String refresh_token) throws SQLException, RuntimeError {
        ArrayList<User> listOfUser = new UserDao().execQuery("refresh_token", refresh_token);
        if (listOfUser.isEmpty()) {
            throw new RuntimeError("No such refresh_token", 121);
        }
        User user = listOfUser.get(0);
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
        User user = new UserDao().execQuery(userid);
        if (user == null) {
            throw new RuntimeError("No Such UserID!", 101);
        }
        String uuid = String.valueOf(UUID.randomUUID());

        new UserDao().execUpdate("refresh_token", uuid, userid);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sdfStr = sdf.format(new Date(new Date().getTime() + refreshTokenAgeAdder));
        new UserDao().execUpdate("refresh_token_age", sdfStr, userid);
        return uuid;
    }

    public static User login(String userid, String password) throws SQLException, RuntimeError {
        User user = new UserDao().execQuery(userid);
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
        if (user.getOp() == 0 || user.getOp() == 10) {
            updateAccessToken(user.getUserid());
            updateRefreshToken(user.getUserid());
        }
        user = new UserDao().execQuery(userid);
        return user;
    }

    public static User register(String userid, String password, int op) throws SQLException, RuntimeError {
        return register(userid, null, password, op);
    }

    public static User register(String userid, String username, String password, int op) throws SQLException, RuntimeError {
        User userCheck = new UserDao().execQuery(userid);
        if (userCheck != null) {
            throw new RuntimeError("This UserID already exists!", 107);
        }
        User user = new User(op);
        user.setUserid(userid);
        if (username != null && !Objects.equals(username.trim(), "")) {
            user.setUsername(username.trim());
        } else {
            user.setUsername("飞翔的小猪" + userid);
        }
        user.setPassword(password);
        user.setAccess_token("register");
        user.setRefresh_token("register");

        new UserDao().execInsert(user);
        updateAccessToken(user.getUserid());
        updateRefreshToken(user.getUserid());

        user = new UserDao().execQuery(userid);
        return user;
    }

    public static void changePassword(String accessToken, String oldPassword, String newPassword) throws RuntimeError, SQLException {
        User user = verifyAccessToken(accessToken);
        String userid = user.getUserid();
        if (Objects.equals(oldPassword, newPassword)) {
            throw new RuntimeError("The old password is the same as the new password!", 501);
        }
        if (Objects.equals(user.getPassword(), oldPassword)) {
            new UserDao().execUpdate("password", newPassword, userid);
        } else {
            throw new RuntimeError("The password is incorrect!", 102);
        }
        UserService.updateAccessToken(userid);
        UserService.updateRefreshToken(userid);
    }

}
