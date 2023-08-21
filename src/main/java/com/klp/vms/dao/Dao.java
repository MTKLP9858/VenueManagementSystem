package com.klp.vms.dao;

import com.klp.vms.entity.User;

import java.sql.SQLException;
import java.util.List;

public interface Dao {
    String defaultDataBaseUrl = "main.db";

    void connect() throws Exception;

    void connect(String dataBaseName) throws Exception;

    void disConnect();

    void execInsert(User user) throws SQLException;

    void execDelete(String KeyColumn) throws SQLException;

    List execQuery(String column, String value) throws SQLException;

    void execUpdate(String column, String value, String KeyColumn) throws SQLException;
}
