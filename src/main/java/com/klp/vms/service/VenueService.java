package com.klp.vms.service;

import com.klp.vms.dao.VenueDao;
import com.klp.vms.entity.Order;
import com.klp.vms.entity.User;
import com.klp.vms.entity.Venue;
import com.klp.vms.exception.RuntimeError;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;
import java.util.Objects;

public class VenueService {
    public static String getUUID(String name, String area, String stadium) throws SQLException {
        return new VenueDao().getUUID(name, area, stadium);
    }

    public static void verifyAdminOfVenueByUUID(String accessToken, String uuid) throws SQLException, RuntimeError, ParseException {
        User user = UserService.verifyAccessToken(accessToken);
        if (user.getOp() == User.OP.USER) throw new RuntimeError("Permission denied", 1102);
        String stadiumFromUUID = new VenueDao().execQuery(uuid).getStadium();
        String adminUserID = StadiumService.getAdminUser(stadiumFromUUID).getUserid();
        if (!Objects.equals(user.getUserid(), adminUserID) && user.getOp() == User.OP.ADMIN) {
            throw new RuntimeError("You are not the administrator of this venue!Permission denied!", 1105);
        }
    }

    public static byte[] queryImg(String accessToken, int imgIndex, String uuid) throws SQLException, RuntimeError, ParseException {
        User user = UserService.verifyAccessToken(accessToken);
//        if (user.getOp() == User.OP.USER) throw new RuntimeError("Permission denied", 270);
        String stadiumFromUUID = new VenueDao().execQuery(uuid).getStadium();
        String adminUserID = StadiumService.getAdminUser(stadiumFromUUID).getUserid();
        if (!Objects.equals(user.getUserid(), adminUserID) && user.getOp() == User.OP.ADMIN) {
            throw new RuntimeError("You are not the administrator of this venue!Permission denied!", 1105);
        }
        int size = new VenueDao().getSizeOfImageList(uuid);
        if (imgIndex < size) {
            return new VenueDao().imgQuery(imgIndex, uuid);
        } else {
            throw new RuntimeError("IndexOutOfBoundsException: The index you input is bigger then image_list's size", 1506);
        }
    }

    public static void addImg(String accessToken, @NotNull MultipartFile img, String uuid) throws SQLException, RuntimeError, ParseException {
        verifyAdminOfVenueByUUID(accessToken, uuid);

        int oldSize = new VenueDao().getSizeOfImageList(uuid);
        new VenueDao().imgInsert(oldSize, img, uuid);
    }

    public static boolean deleteImg(String accessToken, int imgIndex, String uuid) throws SQLException, RuntimeError, ParseException {
        verifyAdminOfVenueByUUID(accessToken, uuid);

        int size = new VenueDao().getSizeOfImageList(uuid);
        if (imgIndex < size) {
            return new VenueDao().imgDelete(imgIndex, uuid);
        } else {
            throw new RuntimeError("IndexOutOfBoundsException: The index you input is bigger then image_list's size", 1506);
        }
    }

    public static boolean updateImg(String accessToken, int index, @NotNull MultipartFile img, String uuid) throws SQLException, RuntimeError, ParseException {
        verifyAdminOfVenueByUUID(accessToken, uuid);
        return new VenueDao().imgUpdate(index, img, uuid);
    }

    public static int add(String accessToken, String name, String area, String stadium, double price, String introduction) throws SQLException, RuntimeError, ParseException {
        User user = UserService.verifyAccessToken(accessToken);
        if (user.getOp() == User.OP.USER) throw new RuntimeError("Permission denied", 1102);
        String adminUserID = StadiumService.getAdminUser(stadium).getUserid();
        if (!Objects.equals(user.getUserid(), adminUserID) && user.getOp() == User.OP.ADMIN) {
            throw new RuntimeError("You are not the administrator of this venue! Permission denied!", 1105);
        }
        Venue venue = new Venue();
        venue.setName(name);
        venue.setArea(area);
        venue.setStadium(stadium);
        venue.setState(Venue.STATE.OPENED);
        venue.setPrice(price);
        venue.setIntroduction(introduction);
        return new VenueDao().execInsert(venue);
    }

    public static int delete(String accessToken, String uuid) throws SQLException, RuntimeError, ParseException {
        User user = UserService.verifyAccessToken(accessToken);
        if (user.getOp() == User.OP.USER) throw new RuntimeError("Permission denied", 1102);
        Venue venue = new VenueDao().execQuery(uuid);
        String stadiumFromUUID = venue.getStadium();
        String adminUserID = StadiumService.getAdminUser(stadiumFromUUID).getUserid();
        if (!Objects.equals(user.getUserid(), adminUserID) && user.getOp() == User.OP.ADMIN) {
            throw new RuntimeError("You are not the administrator of this venue! Permission denied!", 1105);
        }
        if (Objects.equals(venue.getState(), Venue.STATE.CLOSED)) {
            return new VenueDao().execDelete(uuid);
        } else {
            throw new RuntimeError("The venue has not been completely closed!", 1312);
        }
    }

    public static Venue query(String uuid) throws SQLException, RuntimeError, ParseException {
        return new VenueDao().execQuery(uuid);
    }

    public static Venue query(String accessToken, String uuid) throws SQLException, RuntimeError, ParseException {
        User user = UserService.verifyAccessToken(accessToken);
//        if (user.getOp() == User.OP.USER) throw new RuntimeError("Permission denied", 270);
        String stadiumFromUUID = new VenueDao().execQuery(uuid).getStadium();
        String adminUserID = StadiumService.getAdminUser(stadiumFromUUID).getUserid();
        if (!Objects.equals(user.getUserid(), adminUserID) && user.getOp() == User.OP.ADMIN) {
            throw new RuntimeError("You are not the administrator of this venue! Permission denied!", 1105);
        }
        return new VenueDao().execQuery(uuid);
    }

    public static int update(String accessToken, String uuid, String column, Object value) throws RuntimeError, SQLException, ParseException {
        verifyAdminOfVenueByUUID(accessToken, uuid);
        if (column != null) {
            switch (column) {
                case "name", "area", "stadium", "introduction", "price" -> {//限制column为Stadium的列名
                    return new VenueDao().execUpdate(column, value, uuid);
                }
                default -> throw new RuntimeError("Illegal column!", 1502);
            }
        }
        return -1;
    }

    public static void setPrice(String accessToken, String uuid, double price) throws RuntimeError, SQLException, ParseException {
        verifyAdminOfVenueByUUID(accessToken, uuid);
        if (price >= 0) {
            new VenueDao().execUpdate("state", price, uuid);
        } else {
            throw new RuntimeError("The price cannot be negative", 1313);
        }
    }

    public static String getPrice(String uuid) throws SQLException, RuntimeError, ParseException {
        return new VenueDao().execQuery(uuid).getState();
    }

    public static void close(String uuid) throws SQLException, RuntimeError, ParseException {
        Venue venue = VenueService.query(uuid);
        List<Order> orders = OrderService.queryOrderByVenueUUID(uuid);
        orders = OrderService.StateFilter(orders, new String[]{Order.STATE.PAID, Order.STATE.USING, Order.STATE.PAYING});
        if (!orders.isEmpty()) {
            new VenueDao().execUpdate("state", Venue.STATE.CLOSING, uuid);
            throw new RuntimeError("Some orders are not yet fulfilled, so this venue cannot be closed! But it's closing now.", 1314);
        } else {
            new VenueDao().execUpdate("state", Venue.STATE.CLOSED, uuid);
        }
    }

    public static void open(String uuid) throws SQLException, RuntimeError, ParseException {
        Venue venue = VenueService.query(uuid);
        if (Objects.equals(venue.getState(), Venue.STATE.OPENED)) {
            throw new RuntimeError("Already opened!", 1308);
        }
        if (Objects.equals(venue.getState(), Venue.STATE.CLOSING) || Objects.equals(venue.getState(), Venue.STATE.CLOSED)) {
            new VenueDao().execUpdate("state", Venue.STATE.OPENED, uuid);
        }
    }

}


