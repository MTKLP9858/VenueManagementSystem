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
        sql.append(stadium.getName() == null ? "NULL" : ("'" + stadium.getName().replaceAll("'", "''") + "'")).append(",");
        sql.append(stadium.getAddress() == null ? "NULL" : ("'" + stadium.getAddress().replaceAll("'", "''") + "'")).append(",");
        sql.append(stadium.getIntroduction() == null ? "NULL" : ("'" + stadium.getIntroduction().replaceAll("'", "''") + "'")).append(",");
        sql.append(stadium.getContact() == null ? "NULL" : ("'" + stadium.getContact().replaceAll("'", "''") + "'")).append(",");
        sql.append(stadium.getAdminUserID() == null ? "NULL" : ("'" + stadium.getAdminUserID().replaceAll("'", "''") + "'"));
        sql.append(");");
        this.update(String.valueOf(sql));
    }

    @Override
    public void execDelete(String name) throws RuntimeError {
        this.update("delete FROM Stadium where name='" + name.replaceAll("'", "''") + "';");
    }

    @Override
    public List<Stadium> execQuery(String column, String value) throws SQLException, RuntimeError {
        if (value == null) return null;
        String sql = "select * from Stadium where " + column.replaceAll("'", "''") + "='" + value.replaceAll("'", "''") + "';";
        ArrayList<Stadium> list = new ArrayList<>();
        ResultSet rs = this.query(sql);
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
        this.close();
        return list;
    }

    @Override
    public void execUpdate(String column, String value, String name) throws RuntimeError {
        StringBuilder sql = new StringBuilder("UPDATE Stadium SET ");
        sql.append(column.replaceAll("'", "''") + "=");
        sql.append("'" + value.replaceAll("'", "''") + "'");
        sql.append(" WHERE name=");
        sql.append("'" + name.replaceAll("'", "''") + "'");
        this.update(String.valueOf(sql));
    }
}
