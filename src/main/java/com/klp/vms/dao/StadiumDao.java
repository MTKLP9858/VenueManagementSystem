package com.klp.vms.dao;

import com.klp.vms.entity.Stadium;
import com.klp.vms.entity.Venue;
import com.klp.vms.exception.RuntimeError;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StadiumDao implements Dao<Stadium> {
    Statement statement;
    Connection connection;

    @Override
    public void connect() throws Exception {
        connect(defaultDataBaseUrl);
    }

    @Override
    public void connect(String dataBaseName) throws RuntimeError {
        try {
            statement.close();
            connection.close();
        } catch (Exception ignored) {
        }
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeError("Missing org.sqlite.JDBC, please check the server environment!", 10);
        }
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + dataBaseName);
            statement = connection.createStatement();
        } catch (SQLException e) {
            throw new RuntimeError("Database error, check if the database path or data table exists", 11);
        }
    }

    @Override
    public void disConnect() {
        try {
            statement.close();
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void execInsert(Stadium stadium) throws SQLException {
        StringBuilder sql = new StringBuilder("insert into Stadium (name, address, introduction, contact, adminUserID) VALUES (");
        sql.append(stadium.getName() == null ? "NULL" : ("'" + stadium.getName()) + "'").append(",");
        sql.append(stadium.getAddress() == null ? "NULL" : ("'" + stadium.getAddress()) + "'").append(",");
        sql.append(stadium.getIntroduction() == null ? "NULL" : ("'" + stadium.getIntroduction()) + "'").append(",");
        sql.append(stadium.getContact() == null ? "NULL" : ("'" + stadium.getContact()) + "'").append(",");
        sql.append(stadium.getAdminUserID() == null ? "NULL" : ("'" + stadium.getAdminUserID()) + "'");
        sql.append(");");
        statement.executeUpdate(String.valueOf(sql));
    }

    @Override
    public void execDelete(String name) throws SQLException {
        statement.executeUpdate("delete FROM Stadium where name='" + name + "';");
    }

    @Override
    public List<Stadium> execQuery(String column, String value) throws SQLException, RuntimeError {
        if (value == null) return null;
        String sql = "select * from Stadium where " + column + "='" + value + "';";
        ResultSet rs = statement.executeQuery(sql);
        ArrayList<Stadium> list = new ArrayList<>();
        while (rs.next()) {
            Stadium stadium = new Stadium();
            stadium.setName(rs.getString("name"));
            stadium.setAddress(rs.getString("address"));
            stadium.setIntroduction(rs.getString("introduction"));
            stadium.setContact(rs.getString("contact"));
            stadium.setAdminUserID(rs.getString("adminUserID"));
            UserDao userDao = new UserDao();
            stadium.setAdminUser(userDao.execQuery(stadium.getAdminUserID()));
            userDao.disConnect();

            ArrayList<Venue> venueList;
            VenueDao venueDao = new VenueDao();
            venueList = venueDao.execQuery("stadium", stadium.getName());
            venueDao.disConnect();

            stadium.setVenues(venueList);

            list.add(stadium);
        }
        return list;
    }

    @Override
    public void execUpdate(String column, String value, String name) throws SQLException {
        StringBuilder sql = new StringBuilder("UPDATE Stadium SET ");
        sql.append(column + "=");
        sql.append("'" + value + "'");
        sql.append(" WHERE name=");
        sql.append("'" + name + "'");
        statement.executeUpdate(String.valueOf(sql));
    }
}
