package com.klp.vms.service;

import com.klp.vms.dao.ImageDao;
import com.klp.vms.exception.RuntimeError;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Iterator;

public class ImageService {
    public static boolean isImage(File file) {
        if (file != null && file.exists() && file.isFile()) {
            try (ImageInputStream iis = ImageIO.createImageInputStream(file)) {
                Iterator<ImageReader> iter = ImageIO.getImageReaders(iis);
                if (iter.hasNext()) return true;
            } catch (IOException e) {
                return false;
            }
        }
        return false;
    }

    public static String add(@NotNull MultipartFile img) throws RuntimeError {
        File file = new File(ImageDao.ImgTempPath + img.getOriginalFilename());
        try {
            FileUtils.copyInputStreamToFile(img.getInputStream(), file);
            if (!isImage(file)) throw new RuntimeError("image was broken, add failed", 165);
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
            if (!isImage(file)) {
                delete(index);
                throw new RuntimeError("image was broken, query failed", 165);
            }
            return Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            throw new RuntimeError(e.getMessage(), 151);
        }
    }

    public static boolean delete(String index) throws RuntimeError {
        return new ImageDao().execDelete(index);
    }

    public static boolean update(String index, @NotNull MultipartFile img) throws RuntimeError {
        File file = new File(ImageDao.ImgTempPath + img.getOriginalFilename());
        try {
            FileUtils.copyInputStreamToFile(img.getInputStream(), file);
            if (!isImage(file)) throw new RuntimeError("image was broken, update failed", 165);
            return new ImageDao().execUpdate(index, file);
        } catch (IOException e) {
            throw new RuntimeError(e.getMessage(), 151);
        }
    }
}
