package com.klp.vms.dao;

import com.klp.vms.exception.RuntimeError;

import java.io.*;
import java.sql.*;
import java.util.UUID;

import static com.klp.vms.dao.Dao.defaultDataBaseUrl;

public class ImageDao {
    public final static String imgTempPath = "temp" + File.separator + "img" + File.separator;

    public ImageDao() {
        File file = new File(imgTempPath);
        File[] files = file.listFiles();
        if (files != null) {
            for (File f : files) {
                f.delete();
            }
        }
    }

    public String execInsert(File img) throws RuntimeError {
        if (img == null) return null;
        if (!new File(imgTempPath).exists()) new File(imgTempPath).mkdirs();
        String sql = "INSERT INTO image_list(img_index,img_file) VALUES(?,?)";
        try (FileInputStream fis = new FileInputStream(img); Connection conn = DriverManager.getConnection("jdbc:sqlite:" + defaultDataBaseUrl); PreparedStatement stat = conn.prepareStatement(sql)) {
            String uuid = String.valueOf(UUID.randomUUID());
            stat.setString(1, uuid);
            stat.setBytes(2, fis.readAllBytes());
            stat.executeUpdate();
            return uuid;
        } catch (IOException e) {
            throw new RuntimeError("FileInputStream error, file or path to file doesn't exists", 154);
        } catch (SQLException e) {
            throw new RuntimeError("Database error, check if the database path or data table exists", 11);
        }
    }

    public void execDelete(String index) throws RuntimeError {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeError("Missing org.sqlite.JDBC, please check the server environment!", 10);
        }
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + defaultDataBaseUrl); Statement statement = connection.createStatement()) {
            statement.executeUpdate("delete FROM image_list where img_index='" + index + "';");
        } catch (SQLException e) {
            throw new RuntimeError("Database error, check if the database path or data table exists", 11);
        }
    }

    public File execQuery(String index) throws RuntimeError {
        if (index == null) return null;
        if (!new File(imgTempPath).exists()) new File(imgTempPath).mkdirs();
        File file = new File(imgTempPath + index + ".png");
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + defaultDataBaseUrl); Statement statement = connection.createStatement()) {
            ResultSet rs = statement.executeQuery("select * from image_list where img_index ='" + index + "';");
            if (rs.next()) {
                byte[] bytes = rs.getBytes("img_file");
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    fos.write(bytes);
                }
            } else {
                return null;
            }
        } catch (IOException e) {
            throw new RuntimeError("FileInputStream error, file or path to file doesn't exists", 154);
        } catch (SQLException e) {
            throw new RuntimeError("Database error, check if the database path or data table exists", 11);
        }
        return file;
    }

    public boolean execUpdate(String index, File img) throws RuntimeError {
        if (index == null || img == null) return false;
        if (!new File(imgTempPath).exists()) new File(imgTempPath).mkdirs();
        String sql = "UPDATE image_list SET img_file=? where img_index=?;";
        try (FileInputStream fis = new FileInputStream(img); Connection conn = DriverManager.getConnection("jdbc:sqlite:" + defaultDataBaseUrl); PreparedStatement stat = conn.prepareStatement(sql);) {
            stat.setBytes(1, fis.readAllBytes());
            stat.setString(2, index);
            int r = stat.executeUpdate();
            return r > 0;
        } catch (IOException e) {
            throw new RuntimeError("FileInputStream error, file or path to file doesn't exists", 154);
        } catch (SQLException e) {
            throw new RuntimeError("Database error, check if the database path or data table exists", 11);
        }
    }
}
