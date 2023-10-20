package com.klp.vms.service;

import com.klp.vms.dao.OrderDao;
import com.klp.vms.dao.StadiumDao;
import com.klp.vms.entity.Order;
import com.klp.vms.entity.Stadium;
import com.klp.vms.entity.User;
import com.klp.vms.exception.RuntimeError;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class StadiumService {
    public static Stadium getStadiumByAdminAccessToken(String adminAccessToken) throws SQLException, RuntimeError, ParseException {
        User user = UserService.verifyAccessToken(adminAccessToken);
        if (user.getOp() != User.OP.ADMIN) {
            throw new RuntimeError("The AdminAccessToken you input is not an admin!", 282);
        }
        List<Stadium> stadiumList = new StadiumDao().execQuery("adminUserID", user.getUserid());
        if (stadiumList.size() != 1) {
            throw new RuntimeError("Can't find this name from all the stadiums! Or more than one stadiums have the same name!", 280);
        }
        return stadiumList.get(0);
    }

    public static User getAdminUser(String stadiumName) throws SQLException, RuntimeError, ParseException {
        List<Stadium> stadiumList = new StadiumDao().execQuery("name", stadiumName);
        if (stadiumList.size() != 1) {//每个球馆只能有一个管理员，所以
            throw new RuntimeError("Can't find this name from all the stadiums! Or more than one stadiums have the same name!", 280);
        }
        return stadiumList.get(0).getAdminUser();
    }

    public static void verifyAdminOfStadiumByName(String accessToken, String name) throws SQLException, RuntimeError, ParseException {
        User user = UserService.verifyAccessToken(accessToken);
        if (user.getOp() == User.OP.USER) throw new RuntimeError("Permission denied", 270);

        String adminUserID = StadiumService.getAdminUser(name).getUserid();
        if (!Objects.equals(user.getUserid(), adminUserID) || user.getOp() != User.OP.ADMIN) {
            throw new RuntimeError("You are not the administrator of this stadium! Permission denied!", 271);
        }
    }

    public static byte[] queryImg(String accessToken, int imgIndex, String name) throws SQLException, RuntimeError, ParseException {
        User user = UserService.verifyAccessToken(accessToken);
//        if (user.getOp() == User.OP.USER) throw new RuntimeError("Permission denied", 270);

        String adminUserID = StadiumService.getAdminUser(name).getUserid();
        if (!Objects.equals(user.getUserid(), adminUserID) && user.getOp() == User.OP.ADMIN) {
            throw new RuntimeError("You are not the administrator of this stadium! Permission denied!", 271);
        }

        int size = new StadiumDao().getSizeOfImageList(name);
        if (imgIndex < size) {
            return new StadiumDao().imgQuery(imgIndex, name);
        } else {
            throw new RuntimeError("IndexOutOfBoundsException: The index you input is bigger then image_list's size", 269);
        }
    }

    public static void addImg(String accessToken, @NotNull MultipartFile img, String name) throws SQLException, RuntimeError, ParseException {
        verifyAdminOfStadiumByName(accessToken, name);

        int oldSize = new StadiumDao().getSizeOfImageList(name);
        new StadiumDao().imgInsert(oldSize, img, name);
    }

    public static boolean deleteImg(String accessToken, int imgIndex, String name) throws SQLException, RuntimeError, ParseException {
        verifyAdminOfStadiumByName(accessToken, name);

        int size = new StadiumDao().getSizeOfImageList(name);
        if (imgIndex < size) {
            return new StadiumDao().imgDelete(imgIndex, name);
        } else {
            throw new RuntimeError("IndexOutOfBoundsException: The index you input is bigger then image_list's size", 269);
        }
    }

    public static boolean updateImg(String accessToken, int index, @NotNull MultipartFile img, String name) throws SQLException, RuntimeError, ParseException {
        verifyAdminOfStadiumByName(accessToken, name);

        return new StadiumDao().imgUpdate(index, img, name);
    }

    public static int add(String accessToken, String name, String address, String introduction, String contact, String adminUserID) throws SQLException, RuntimeError {
        User user = UserService.verifyAccessToken(accessToken);
        if (user.getOp() != User.OP.SU) throw new RuntimeError("Permission denied", 270);
        User userOfAdmin = UserService.verifyAccessToken(accessToken);
        if (userOfAdmin.getOp() != User.OP.ADMIN) {
            throw new RuntimeError("The adminUserID entered is not an administrator!", 503);
        }
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

    public static Stadium query(String accessToken, String name) throws SQLException, RuntimeError, ParseException {
        User user = UserService.verifyAccessToken(accessToken);
//        if (user.getOp() == User.OP.USER) throw new RuntimeError("Permission denied", 270);
        if (user.getOp() == User.OP.ADMIN) {
            List<Stadium> stadiumList = new StadiumDao().execQuery("name", name);
            if (stadiumList.size() != 1) {
                throw new RuntimeError("Can't find this name from all the stadiums! Or more than one stadiums have the same name!", 280);
            }
            Stadium stadium = stadiumList.get(0);
            if (Objects.equals(stadium.getAdminUserID(), user.getUserid()) && user.getOp() == User.OP.ADMIN) {
                return stadium;
            } else {
                throw new RuntimeError("Permission denied: you are not the Admin of this stadium!", 500);
            }
        }
        List<Stadium> stadiumList = new StadiumDao().execQuery("name", name);
        if (stadiumList.size() != 1) {
            throw new RuntimeError("Can't find this name from all the stadiums! Or more than one stadiums have the same name!", 280);
        }
        return stadiumList.get(0);
    }


    public static List<Order> queryAllOrders(String accessToken, String name) throws SQLException, RuntimeError, ParseException {
        List<Order> orders = new OrderDao().execQuery("stadiumName", name);
        return orders;
    }


    public static int update(String accessToken, String name, String column, Object value) throws RuntimeError, SQLException, ParseException {
        User user = UserService.verifyAccessToken(accessToken);
        if (user.getOp() == User.OP.USER) throw new RuntimeError("Permission denied", 270);
        switch (column) {
            case "name", "address", "introduction", "contact", "adminUserID" -> {//限制column为Stadium的列名
                List<Stadium> stadiumList = new StadiumDao().execQuery("name", name);
                if (stadiumList.size() != 1) {
                    throw new RuntimeError("Can't find this name from all the stadiums! Or more than one stadiums have the same name!", 280);
                }
                Stadium stadium = stadiumList.get(0);
                if (Objects.equals(stadium.getAdminUserID(), user.getUserid()) && user.getOp() == User.OP.ADMIN) {//验证是否为管理员
                    return new StadiumDao().execUpdate(column, value, name);
                } else {
                    throw new RuntimeError("Permission denied: you are not the Admin of this stadium!", 500);
                }
            }
            default -> throw new RuntimeError("Illegal column!", 283);
        }
    }


}
