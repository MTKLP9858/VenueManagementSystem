package com.klp.vms.service;

import com.klp.vms.dao.ImageDao;
import com.klp.vms.exception.RuntimeError;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class ImageService {
    public static String add(@NotNull MultipartFile img) throws RuntimeError {
        File file = new File(ImageDao.imgPath + img.getOriginalFilename());
        try {
            FileUtils.copyInputStreamToFile(img.getInputStream(), file);
            ImageDao imageDao = new ImageDao();
            return imageDao.execInsert(file);
        } catch (IOException e) {
            throw new RuntimeError("FileInputStream error, file or path to file doesn't exists", 154);
        }
    }

    public static byte[] query(String index) throws RuntimeError {
        ImageDao imageDao = new ImageDao();
        File file = imageDao.execQuery(index);
        try {
            return Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            throw new RuntimeError(e.getMessage(), 151);
        }
    }

    public static void delete(String index) throws RuntimeError {
        new ImageDao().execDelete(index);
    }

    public static boolean update(String index, @NotNull MultipartFile img) throws RuntimeError {
        File file = new File(ImageDao.imgPath + img.getOriginalFilename());
        try {
            FileUtils.copyInputStreamToFile(img.getInputStream(), file);
            return new ImageDao().execUpdate(index, file);
        } catch (IOException e) {
            throw new RuntimeError(e.getMessage(), 151);
        }
    }
}
