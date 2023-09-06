package com.klp.vms.service;

import com.klp.vms.dao.VenueDao;
import com.klp.vms.exception.RuntimeError;

import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;

public class VenueService {
    public static byte[] queryImg(int imgIndex, String name, String stadium) throws SQLException, RuntimeError, IOException {
        int size = new VenueDao().getSizeOfImageList(name, stadium);
        if (imgIndex < size) {
            return Files.readAllBytes(new VenueDao().imgQuery(imgIndex, name, stadium).toPath());
        } else {
            throw new RuntimeError("IndexOutOfBoundsException: The index you input is bigger then image_list's size", 269);
        }
    }
}
