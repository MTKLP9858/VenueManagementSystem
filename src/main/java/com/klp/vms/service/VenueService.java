package com.klp.vms.service;

import com.klp.vms.dao.VenueDao;
import com.klp.vms.entity.User;
import com.klp.vms.entity.Venue;
import com.klp.vms.exception.RuntimeError;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.sql.SQLException;
import java.util.Objects;

public class VenueService {
    public static String getUUID(String name, String area, String stadium) throws SQLException {
        return new VenueDao().getUUID(name, area, stadium);
    }

    public static byte[] queryImg(String accessToken, int imgIndex, String uuid) throws SQLException, RuntimeError {
        User user = UserService.verifyAccessToken(accessToken);
//        if (user.getOp() == User.OP.USER) throw new RuntimeError("Permission denied", 270);
        String stadiumFromUUID = new VenueDao().execQuery(uuid).getStadium();
        String adminUserID = StadiumService.getAdminUser(stadiumFromUUID).getUserid();
        if (!Objects.equals(user.getUserid(), adminUserID) && user.getOp() == User.OP.ADMIN) {
            throw new RuntimeError("You are not the administrator of this venue!Permission denied!", 271);
        }
        int size = new VenueDao().getSizeOfImageList(uuid);
        if (imgIndex < size) {
            return new VenueDao().imgQuery(imgIndex, uuid);
        } else {
            throw new RuntimeError("IndexOutOfBoundsException: The index you input is bigger then image_list's size", 269);
        }
    }

    public static void addImg(String accessToken, @NotNull MultipartFile img, String uuid) throws SQLException, RuntimeError {
        User user = UserService.verifyAccessToken(accessToken);
        if (user.getOp() == User.OP.USER) throw new RuntimeError("Permission denied", 270);
        String stadiumFromUUID = new VenueDao().execQuery(uuid).getStadium();
        String adminUserID = StadiumService.getAdminUser(stadiumFromUUID).getUserid();
        if (!Objects.equals(user.getUserid(), adminUserID) && user.getOp() == User.OP.ADMIN) {
            throw new RuntimeError("You are not the administrator of this venue!Permission denied!", 271);
        }

        int oldSize = new VenueDao().getSizeOfImageList(uuid);
        new VenueDao().imgInsert(oldSize, img, uuid);
    }

    public static boolean deleteImg(String accessToken, int imgIndex, String uuid) throws SQLException, RuntimeError {
        User user = UserService.verifyAccessToken(accessToken);
        if (user.getOp() == User.OP.USER) throw new RuntimeError("Permission denied", 270);
        String stadiumFromUUID = new VenueDao().execQuery(uuid).getStadium();
        String adminUserID = StadiumService.getAdminUser(stadiumFromUUID).getUserid();
        if (!Objects.equals(user.getUserid(), adminUserID) && user.getOp() == User.OP.ADMIN) {
            throw new RuntimeError("You are not the administrator of this venue!Permission denied!", 271);
        }

        int size = new VenueDao().getSizeOfImageList(uuid);
        if (imgIndex < size) {
            return new VenueDao().imgDelete(imgIndex, uuid);
        } else {
            throw new RuntimeError("IndexOutOfBoundsException: The index you input is bigger then image_list's size", 269);
        }
    }

    public static boolean updateImg(String accessToken, int index, @NotNull MultipartFile img, String uuid) throws SQLException, RuntimeError {
        User user = UserService.verifyAccessToken(accessToken);
        if (user.getOp() == User.OP.USER) throw new RuntimeError("Permission denied", 270);
        String stadiumFromUUID = new VenueDao().execQuery(uuid).getStadium();
        String adminUserID = StadiumService.getAdminUser(stadiumFromUUID).getUserid();
        if (!Objects.equals(user.getUserid(), adminUserID) && user.getOp() == User.OP.ADMIN) {
            throw new RuntimeError("You are not the administrator of this venue!Permission denied!", 271);
        }
        return new VenueDao().imgUpdate(index, img, uuid);
    }

    public static int add(String accessToken, String name, String area, String stadium, double price, String introduction) throws SQLException, RuntimeError {
        User user = UserService.verifyAccessToken(accessToken);
        if (user.getOp() == User.OP.USER) throw new RuntimeError("Permission denied", 270);
        String adminUserID = StadiumService.getAdminUser(stadium).getUserid();
        if (!Objects.equals(user.getUserid(), adminUserID) && user.getOp() == User.OP.ADMIN) {
            throw new RuntimeError("You are not the administrator of this venue! Permission denied!", 271);
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

    public static int delete(String accessToken, String uuid) throws SQLException, RuntimeError {
        User user = UserService.verifyAccessToken(accessToken);
        if (user.getOp() == User.OP.USER) throw new RuntimeError("Permission denied", 270);
        Venue venue = new VenueDao().execQuery(uuid);
        String stadiumFromUUID = venue.getStadium();
        String adminUserID = StadiumService.getAdminUser(stadiumFromUUID).getUserid();
        if (!Objects.equals(user.getUserid(), adminUserID) && user.getOp() == User.OP.ADMIN) {
            throw new RuntimeError("You are not the administrator of this venue! Permission denied!", 271);
        }
        if (Objects.equals(venue.getState(), Venue.STATE.CLOSED)) {
            return new VenueDao().execDelete(uuid);
        } else {
            throw new RuntimeError("The venue has not been completely closed!", 310);
        }
    }

    public static Venue query(String accessToken, String uuid) throws SQLException, RuntimeError {
        User user = UserService.verifyAccessToken(accessToken);
        if (user.getOp() == User.OP.USER) throw new RuntimeError("Permission denied", 270);
        String stadiumFromUUID = new VenueDao().execQuery(uuid).getStadium();
        String adminUserID = StadiumService.getAdminUser(stadiumFromUUID).getUserid();
        if (!Objects.equals(user.getUserid(), adminUserID) && user.getOp() == User.OP.ADMIN) {
            throw new RuntimeError("You are not the administrator of this venue! Permission denied!", 271);
        }
        return new VenueDao().execQuery(uuid);
    }

}
