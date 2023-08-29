package com.klp.vms.service;

import com.klp.vms.dao.ImageDao;
import com.klp.vms.dao.UserDao;
import com.klp.vms.entity.User;
import com.klp.vms.exception.RuntimeError;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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


    public static boolean updateAvatar(String access_token, @NotNull MultipartFile img) throws RuntimeError, SQLException {
        File file = new File(ImageDao.imgPath + img.getOriginalFilename());
        if (!isImage(file)) throw new RuntimeError("avatar image broken, please upload again", 155);
        try {
            User user = verifyAccessToken(access_token);
            FileUtils.copyInputStreamToFile(img.getInputStream(), file);
            return new UserDao().updateAvatar(user.getUserid(), file);
        } catch (IOException e) {
            throw new RuntimeError(e.getMessage(), 151);
        }
    }

    public static boolean isImage(File file) {
        if (file != null && file.exists() && file.isFile()) {
            try {
                BufferedImage bi = ImageIO.read(file);
                if (bi != null) {
                    return true;
                }
            } catch (IOException e) {
                return false;
            }
        }
        return false;
    }

    public static byte[] queryAvatar(String access_token) throws RuntimeError, SQLException {
        UserDao userDao = new UserDao();
        User user = verifyAccessToken(access_token);
        File file = userDao.queryAvatar(user.getUserid());
        if (file == null) throw new RuntimeError("avatar not found", 156);
        if (!isImage(file)) throw new RuntimeError("avatar image broken, please upload again", 155);
        try {
            return Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            throw new RuntimeError(e.getMessage(), 151);
        }
    }

    public static void rename(String newUsername, String access_token) throws SQLException, RuntimeError {
        User user = verifyAccessToken(access_token);
        if (Objects.equals(user.getUsername(), newUsername)) {
            throw new RuntimeError("The new name duplicates the old name!", 120);
        }
        new UserDao().execUpdate("username", newUsername, user.getUserid());
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
        User user = new UserDao().execQueryBy("access_token", access_token);
        if (user == null) {
            throw new RuntimeError("No such access_token", 111);
        }
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
        User user = new UserDao().execQueryBy("refresh_token", refresh_token);
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
        if (user.getOp() == 0) {
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
        user.setUsername(username);
        user.setPassword(password);
        user.setAccess_token("register");
        user.setRefresh_token("register");

        new UserDao().execInsert(user);
        updateAccessToken(user.getUserid());
        updateRefreshToken(user.getUserid());

        user = new UserDao().execQuery(userid);
        return user;
    }

}
