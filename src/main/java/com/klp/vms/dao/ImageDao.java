package com.klp.vms.dao;

import com.klp.vms.exception.RuntimeError;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.UUID;

public class ImageDao {
    public final static String ImgTempPath = System.getProperty("user.dir") + File.separator + "temp" + File.separator + "img" + File.separator;

    public final static long ImageCacheTime = 60 * 60 * 1000;

    public ImageDao() {
        ImageDao.clearOutDateCache();
    }

    public static void clearOutDateCache() {
        File file = new File(ImgTempPath);
        if (!file.isDirectory()) return;
        File[] files = file.listFiles();
        if (files != null) {
            for (File f : files) {
                Date fileOutDate = new Date(f.lastModified() + ImageCacheTime);
                System.out.println(fileOutDate);
                Date nowDate = new Date();
                if (nowDate.after(fileOutDate)) {
                    f.delete();
                }
            }
        }
    }

    public String execInsert(File img) throws RuntimeError {
        if (img == null) return null;
        if (!new File(ImgTempPath).exists()) new File(ImgTempPath).mkdirs();
        String sql = "INSERT INTO image_list(img_index,img_file) VALUES(?,?)";
        try (FileInputStream fis = new FileInputStream(img); Stat stat = new Stat(sql)) {
            String uuid = String.valueOf(UUID.randomUUID());
            stat.setString(1, uuid);
            stat.setBytes(2, fis.readAllBytes());
            stat.executeUpdate();
            return uuid;
        } catch (IOException e) {
            throw new RuntimeError("FileInputStream error: " + e.getMessage() + ", file or path to file doesn't exists", 1504);
        } catch (SQLException e) {
            throw new RuntimeError("Database error: " + e.getMessage() + ", check if the database path or data table exists, and try again!", 9);
        }
    }

    public boolean execDelete(String index) throws RuntimeError {
        String sql = "delete FROM image_list where img_index=?;";
        try (Stat stat = new Stat(sql)) {
            stat.setString(1, index);
            int r = stat.executeUpdate();
            return r > 0;
        } catch (SQLException e) {
            throw new RuntimeError("Database error: " + e.getMessage() + ", check if the database path or data table exists", 9);
        }
    }

    public File execQuery(String index) throws RuntimeError {
        if (index == null) return null;
        if (!new File(ImgTempPath).exists()) new File(ImgTempPath).mkdirs();
        File file = new File(ImgTempPath + index + ".png");
        String sql = "select * from image_list where img_index=?;";
        try (Stat stat = new Stat(sql)) {
            stat.setString(1, index);
            ResultSet rs = stat.executeQuery();
            if (rs.next()) {
                byte[] bytes = rs.getBytes("img_file");
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    fos.write(bytes);
                }
            } else {
                return null;
            }
        } catch (IOException e) {
            throw new RuntimeError("FileInputStream error: " + e.getMessage() + ", file or path to file doesn't exists", 1504);
        } catch (SQLException e) {
            throw new RuntimeError("Database error: " + e.getMessage() + ", check if the database path or data table exists", 9);
        }
        return file;
    }

    public boolean execUpdate(String index, File img) throws RuntimeError {
        if (index == null || img == null) return false;
        if (!new File(ImgTempPath).exists()) new File(ImgTempPath).mkdirs();
        String sql = "UPDATE image_list SET img_file=? where img_index=?;";
        try (FileInputStream fis = new FileInputStream(img); Stat stat = new Stat(sql)) {
            stat.setBytes(1, fis.readAllBytes());
            stat.setString(2, index);
            int r = stat.executeUpdate();
            return r > 0;
        } catch (IOException e) {
            throw new RuntimeError("FileInputStream error: " + e.getMessage() + ", file or path to file doesn't exists", 1504);
        } catch (SQLException e) {
            throw new RuntimeError("Database error: " + e.getMessage() + ", check if the database path or data table exists", 9);
        }
    }
}
