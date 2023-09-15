package com.klp.vms.dao;

import java.util.List;

public interface Dao<T> {
    String defaultDataBaseUrl = "main.db";
    String fullConnectionUrl = "jdbc:sqlite:" + defaultDataBaseUrl;

    int execInsert(T insertValue) throws Exception;

    int execDelete(String KeyColumn) throws Exception;

    List<T> execQuery(String column, Object value) throws Exception;

    int execUpdate(String column, Object value, String KeyIndexValue) throws Exception;
}
