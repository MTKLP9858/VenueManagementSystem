package com.klp.vms.dao;

import java.sql.SQLException;
import java.util.List;

public interface Dao<T> {
    String defaultDataBaseUrl = "main.db";

    void connect() throws Exception;

    void connect(String dataBaseName) throws Exception;

    void disConnect();

    void execInsert(T insertValue) throws Exception;

    void execDelete(String KeyColumn) throws Exception;

    List<T> execQuery(String column, String value) throws Exception;

    void execUpdate(String column, String value, String KeyColumn) throws Exception;
}
