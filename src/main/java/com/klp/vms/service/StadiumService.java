package com.klp.vms.service;

import com.klp.vms.dao.StadiumDao;
import com.klp.vms.entity.Stadium;
import com.klp.vms.entity.User;
import com.klp.vms.exception.RuntimeError;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class StadiumService {
    public static byte[] queryImg(int imgIndex, String uuid) throws SQLException, RuntimeError {
        int size = new StadiumDao().getSizeOfImageList(uuid);
        if (imgIndex < size) {
            return new StadiumDao().imgQuery(imgIndex, uuid);
        } else {
            throw new RuntimeError("IndexOutOfBoundsException: The index you input is bigger then image_list's size", 269);
        }
    }

    public static void addImg(String accessToken, @NotNull MultipartFile img, String uuid) throws SQLException, RuntimeError {
        User user = UserService.verifyAccessToken(accessToken);
        if (user.getOp() == User.OP.USER) throw new RuntimeError("Permission denied", 270);
        int oldSize = new StadiumDao().getSizeOfImageList(uuid);
        new StadiumDao().imgInsert(oldSize, img, uuid);
    }

    public static boolean deleteImg(String accessToken, int imgIndex, String uuid) throws SQLException, RuntimeError {
        User user = UserService.verifyAccessToken(accessToken);
        if (user.getOp() == User.OP.USER) throw new RuntimeError("Permission denied", 270);
        int size = new StadiumDao().getSizeOfImageList(uuid);
        if (imgIndex < size) {
            return new StadiumDao().imgDelete(imgIndex, uuid);
        } else {
            throw new RuntimeError("IndexOutOfBoundsException: The index you input is bigger then image_list's size", 269);
        }
    }

    public static boolean updateImg(String accessToken, int index, @NotNull MultipartFile img, String uuid) throws SQLException, RuntimeError {
        User user = UserService.verifyAccessToken(accessToken);
        if (user.getOp() == User.OP.USER) throw new RuntimeError("Permission denied", 270);
        return new StadiumDao().imgUpdate(index, img, uuid);
    }

    public static int add(String accessToken, String name, String address, String introduction, String contact, String adminUserID) throws SQLException, RuntimeError {
        User user = UserService.verifyAccessToken(accessToken);
        if (user.getOp() != User.OP.SU) throw new RuntimeError("Permission denied", 270);
        Stadium stadium = new Stadium();
        stadium.setName(name);
        stadium.setAddress(address);
        stadium.setIntroduction(introduction);
        stadium.setContact(contact);
        stadium.setAdminUserID(adminUserID);
        return new StadiumDao().execInsert(stadium);
    }

    public static int delete(String accessToken, String name) throws SQLException, RuntimeError {
        User user = UserService.verifyAccessToken(accessToken);
        if (user.getOp() != User.OP.SU) throw new RuntimeError("Permission denied", 270);
        return new StadiumDao().execDelete(name);
    }

    public static Stadium query(String accessToken, String name) throws SQLException, RuntimeError {
        User user = UserService.verifyAccessToken(accessToken);
        if (user.getOp() == User.OP.USER) throw new RuntimeError("Permission denied", 270);
        if (user.getOp() == User.OP.ADMIN) {
            List<Stadium> stadiumList = new StadiumDao().execQuery("name", name);
            if (stadiumList.size() != 1) {
                throw new RuntimeError("Can't find this name from all the stadiums! Or more than one stadiums have the same name!", 280);
            }
            Stadium stadium = stadiumList.get(0);
            if (Objects.equals(stadium.getAdminUserID(), user.getUserid())) {
                return stadium;
            } else {
                throw new RuntimeError("Permission denied: you are not the Admin of this stadium!", 500);
            }
        }
        if (user.getOp() == User.OP.SU) {
            List<Stadium> stadiumList = new StadiumDao().execQuery("name", name);
            if (stadiumList.size() != 1) {
                throw new RuntimeError("Can't find this name from all the stadiums! Or more than one stadiums have the same name!", 280);
            }
            return stadiumList.get(0);
        }
        return null;
    }

    public static void update(String accessToken, String column, String value) {
        //限制column为Stadium的列名
        //验证是否为管理员
    }
}
