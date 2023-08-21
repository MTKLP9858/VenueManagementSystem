package com.klp.vms.dao;

import java.sql.SQLException;
import java.util.List;

public interface Dao<T> {
    String defaultDataBaseUrl = "main.db";

    void connect() throws Exception;

    void connect(String dataBaseName) throws Exception;

    void disConnect();

    void execInsert(T insertValue) throws SQLException;

    void execDelete(String KeyColumn) throws SQLException;

    List<T> execQuery(String column, String value) throws SQLException;

    void execUpdate(String column, String value, String KeyColumn) throws SQLException;
}
