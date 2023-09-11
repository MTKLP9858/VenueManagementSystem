package com.klp.vms.service;

import com.klp.vms.dao.VenueDao;
import com.klp.vms.entity.User;
import com.klp.vms.entity.Venue;
import com.klp.vms.exception.RuntimeError;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.sql.SQLException;

public class VenueService {
    public static byte[] queryImg(int imgIndex, String uuid) throws SQLException, RuntimeError {
        int size = new VenueDao().getSizeOfImageList(uuid);
        if (imgIndex < size) {
            return new VenueDao().imgQuery(imgIndex, uuid);
        } else {
            throw new RuntimeError("IndexOutOfBoundsException: The index you input is bigger then image_list's size", 269);
        }
    }

    public static void addImg(String accessToken, @NotNull MultipartFile img, String uuid) throws SQLException, RuntimeError {
        User user = UserService.verifyAccessToken(accessToken);
        if (user.getOp() == 0) throw new RuntimeError("Permission denied", 270);
        int oldSize = new VenueDao().getSizeOfImageList(uuid);
        new VenueDao().imgInsert(oldSize, img, uuid);
    }

    public static boolean deleteImg(String accessToken, int imgIndex, String uuid) throws SQLException, RuntimeError {
        User user = UserService.verifyAccessToken(accessToken);
        if (user.getOp() == 0) throw new RuntimeError("Permission denied", 270);
        int size = new VenueDao().getSizeOfImageList(uuid);
        if (imgIndex < size) {
            return new VenueDao().imgDelete(imgIndex, uuid);
        } else {
            throw new RuntimeError("IndexOutOfBoundsException: The index you input is bigger then image_list's size", 269);
        }
    }

    public static boolean updateImg(String accessToken, int index, @NotNull MultipartFile img, String uuid) throws SQLException, RuntimeError {
        User user = UserService.verifyAccessToken(accessToken);
        if (user.getOp() == 0) throw new RuntimeError("Permission denied", 270);
        return new VenueDao().imgUpdate(index, img, uuid);
    }

    public static int add(String accessToken, String name, String area, String stadium, double price, String introduction) throws SQLException, RuntimeError {
        User user = UserService.verifyAccessToken(accessToken);
        if (user.getOp() == 0) throw new RuntimeError("Permission denied", 270);
        Venue venue = new Venue();
        venue.setName(name);
        venue.setArea(area);
        venue.setStadium(stadium);
        venue.setState(Venue.STATE.OPENED);
        venue.setPrice(price);
        venue.setIntroduction(introduction);
        return new VenueDao().execInsert(venue);
    }

    public static int delete(String accessToken, String name, String area, String stadium) throws SQLException, RuntimeError {
        User user = UserService.verifyAccessToken(accessToken);
        if (user.getOp() == 0) throw new RuntimeError("Permission denied", 270);
        String uuid = new VenueDao().getUUID(name, area, stadium);
        return new VenueDao().execDelete(uuid);
    }

    public static Venue query(String name, String area, String stadium) throws SQLException {
        String uuid = new VenueDao().getUUID(name, area, stadium);
        return new VenueDao().execQuery(uuid);
    }

}
