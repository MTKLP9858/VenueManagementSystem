package com.klp.vms.dao;

import com.klp.vms.entity.Stadium;
import com.klp.vms.entity.Venue;
import com.klp.vms.exception.RuntimeError;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StadiumDao implements Dao<Stadium> {
    @Override
    public void execInsert(@NotNull Stadium stadium) throws RuntimeError {
        StringBuilder sql = new StringBuilder("insert into Stadium (name, address, introduction, contact, adminUserID) VALUES (");
        sql.append(stadium.getName() == null ? "NULL" : ("'" + stadium.getName() + "'")).append(",");
        sql.append(stadium.getAddress() == null ? "NULL" : ("'" + stadium.getAddress() + "'")).append(",");
        sql.append(stadium.getIntroduction() == null ? "NULL" : ("'" + stadium.getIntroduction() + "'")).append(",");
        sql.append(stadium.getContact() == null ? "NULL" : ("'" + stadium.getContact() + "'")).append(",");
        sql.append(stadium.getAdminUserID() == null ? "NULL" : ("'" + stadium.getAdminUserID() + "'"));
        sql.append(");");
        this.update(String.valueOf(sql));
    }

    @Override
    public void execDelete(String name) throws RuntimeError {
        this.update("delete FROM Stadium where name='" + name + "';");
    }

    @Override
    public List<Stadium> execQuery(String column, String value) throws SQLException, RuntimeError {
        if (value == null) return null;
        String sql = "select * from Stadium where " + column + "='" + value + "';";
        ArrayList<Stadium> list;
        try (ResultSet rs = this.query(sql)) {
            list = new ArrayList<>();
            while (rs.next()) {
                Stadium stadium = new Stadium();
                stadium.setName(rs.getString("name"));
                stadium.setAddress(rs.getString("address"));
                stadium.setIntroduction(rs.getString("introduction"));
                stadium.setContact(rs.getString("contact"));
                stadium.setAdminUserID(rs.getString("adminUserID"));
                stadium.setAdminUser(new UserDao().execQuery(stadium.getAdminUserID()));
                ArrayList<Venue> venueList = new VenueDao().execQuery("stadium", stadium.getName());
                stadium.setVenues(venueList);
                list.add(stadium);
            }
        }
        return list;
    }

    @Override
    public void execUpdate(String column, String value, String name) throws RuntimeError {
        StringBuilder sql = new StringBuilder("UPDATE Stadium SET ");
        sql.append(column + "=");
        sql.append("'" + value + "'");
        sql.append(" WHERE name=");
        sql.append("'" + name + "'");
        this.update(String.valueOf(sql));
    }
}
