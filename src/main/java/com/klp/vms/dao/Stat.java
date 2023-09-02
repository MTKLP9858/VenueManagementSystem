package com.klp.vms.dao;

import lombok.Getter;

import java.sql.*;

public class Stat implements AutoCloseable {
    public static String defaultDataBaseUrl = Dao.defaultDataBaseUrl;
    public static String fullConnectionUrl = "jdbc:sqlite:" + defaultDataBaseUrl;
    Connection conn;
    @Getter
    PreparedStatement stat;

    public Stat(String sql) throws SQLException {
        conn = DriverManager.getConnection(fullConnectionUrl);
        stat = conn.prepareStatement(sql);
    }

    int executeUpdate() throws SQLException {
        return stat.executeUpdate();
    }

    ResultSet executeQuery() throws SQLException {
        return stat.executeQuery();
    }

    ResultSetMetaData getMetaData() throws SQLException {
        return stat.getMetaData();
    }

    @Override
    public void close() {
        try {
            stat.close();
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    Stat setString(int index, String x) throws SQLException {
        stat.setString(index, x);
        return this;
    }

    Stat setBytes(int index, byte[] x) throws SQLException {
        stat.setBytes(index, x);
        return this;
    }

    Stat setDouble(int index, double x) throws SQLException {
        stat.setDouble(index, x);
        return this;
    }

    Stat setLong(int index, long x) throws SQLException {
        stat.setLong(index, x);
        return this;
    }

    Stat setFloat(int index, float x) throws SQLException {
        stat.setFloat(index, x);
        return this;
    }

    Stat setInt(int index, int x) throws SQLException {
        stat.setInt(index, x);
        return this;
    }

    Stat setShort(int index, short x) throws SQLException {
        stat.setShort(index, x);
        return this;
    }

    Stat setByte(int index, byte x) throws SQLException {
        stat.setByte(index, x);
        return this;
    }

    Stat setBoolean(int index, boolean x) throws SQLException {
        stat.setBoolean(index, x);
        return this;
    }

    Stat setNull(int index, int sqlType) throws SQLException {
        stat.setNull(index, sqlType);
        return this;
    }

    Stat setBlob(int index, Blob x) throws SQLException {
        stat.setBlob(index, x);
        return this;
    }

    Stat setArray(int index, Array x) throws SQLException {
        stat.setArray(index, x);
        return this;
    }

    Stat setClob(int index, Clob x) throws SQLException {
        stat.setClob(index, x);
        return this;
    }

}
