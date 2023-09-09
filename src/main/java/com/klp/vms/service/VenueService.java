package com.klp.vms.service;

import com.klp.vms.dao.VenueDao;
import com.klp.vms.entity.User;
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



}
