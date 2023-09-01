package com.klp.vms.dao;

import com.klp.vms.entity.Venue;
import com.klp.vms.exception.RuntimeError;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;

public class VenueDao implements Dao<Venue> {//场地

    @Override
    public void execInsert(Venue venue) throws RuntimeError, SQLException {
        if (venue == null) return;
        if (new StadiumDao().execQuery("name", venue.getStadium()).isEmpty()) {
            throw new RuntimeError("no such value in Stadium.name!", 223);
        }
        if (execQueryBy(venue.getName(), venue.getStadium()) != null) {
            throw new RuntimeError("The same Stadium.name exists!", 223);
        }
        StringBuilder sql = new StringBuilder("insert into Venue (name, area, stadium, introduction, active, price) VALUES (");
        sql.append(venue.getName() == null ? "NULL" : ("'" + venue.getName().replaceAll("'", "''") + "'")).append(",");
        sql.append(venue.getArea() == null ? "NULL" : ("'" + venue.getArea().replaceAll("'", "''") + "'")).append(",");
        sql.append(venue.getStadium() == null ? "NULL" : ("'" + venue.getStadium().replaceAll("'", "''") + "'")).append(",");
        sql.append(venue.getIntroduction() == null ? "NULL" : ("'" + venue.getIntroduction().replaceAll("'", "''") + "'")).append(",");
        sql.append(venue.isActive()).append(",");
        sql.append(venue.getPrice());
        sql.append(");");
        this.update(String.valueOf(sql));
    }

    /**
     * @param stadium Delete all the Venue which stadium eq this param!
     */
    @Override
    public void execDelete(String stadium) throws RuntimeError {
        if (stadium != null) {
            this.update("delete FROM Stadium where stadium='" + stadium.replaceAll("'", "''") + "';");
        }
    }

    public void execDelete(String name, String stadium) throws RuntimeError {
        if (name != null && stadium != null) {
            this.update("delete FROM Stadium where name='" + name.replaceAll("'", "''") + "' and stadium='" + stadium.replaceAll("'", "''") + "';");
        }
    }

    public ArrayList<Venue> execQuery(long price) throws SQLException, RuntimeError {
        return execQuery("price", String.valueOf(price));
    }

    public ArrayList<Venue> execQuery(boolean isActive) throws SQLException, RuntimeError {
        return execQuery("active", isActive ? "1" : "0");
    }

    public Venue execQueryBy(String name, String stadium) throws SQLException, RuntimeError {
        if (stadium == null) return null;
        ArrayList<Venue> listOfName = execQuery("name", name);
        if (listOfName == null) return null;
        ArrayList<Venue> list = new ArrayList<>();
        for (Venue v : listOfName) {
            if (Objects.equals(v.getStadium(), stadium)) {
                list.add(v);
            }
        }
        if (list.size() == 1) {
            return list.get(0);
        } else if (list.isEmpty()) {
            return null;
        } else {
            throw new RuntimeError("There are more than one Venue have the same name and stadium!", 222);
        }
    }

    @Override
    public ArrayList<Venue> execQuery(String column, String value) throws SQLException, RuntimeError {
        if (value == null) return null;
        String sql = "select * from Venue where " + column.replaceAll("'", "''") + "='" + value.replaceAll("'", "''") + "';";
        ArrayList<Venue> list = new ArrayList<>();
        ResultSet rs = this.query(sql);
        while (rs.next()) {
            Venue venue = new Venue();
            venue.setName(rs.getString("name"));
            venue.setArea(rs.getString("area"));
            venue.setStadium(rs.getString("stadium"));
            venue.setIntroduction(rs.getString("introduction"));
            venue.setActive(rs.getBoolean("active"));
            venue.setPrice(rs.getDouble("price"));
            list.add(venue);
        }
        this.close();
        return list;
    }

    /**
     * @deprecated
     */
    @Deprecated
    public void execUpdate(String column, String value, String KEY) {
    }

    public void execUpdate(String column, String value, String name, String stadium) throws RuntimeError, SQLException {
        if (column == null || value == null || name == null || stadium == null) return;
        if (new StadiumDao().execQuery("name", stadium).isEmpty()) {
            throw new RuntimeError("no such value in Stadium.name!", 223);
        }
        if (Objects.equals(column, "stadium")) {
            if (new StadiumDao().execQuery("name", value).isEmpty()) {
                throw new RuntimeError("no such value in Stadium.name!", 223);
            }
        }
        if (Objects.equals(column, "name")) {
            if (execQueryBy(value, stadium) != null) {
                throw new RuntimeError("The same Stadium.name exists!", 224);
            }
        }
        String sql = "UPDATE Venue SET " + column.replaceAll("'", "''") + "='" + value.replaceAll("'", "''") + "'" + " WHERE name='" + name.replaceAll("'", "''") + "' and stadium='" + stadium.replaceAll("'", "''") + "';";
        this.update(sql);
    }
}
