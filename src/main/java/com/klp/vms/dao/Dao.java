package com.klp.vms.dao;

import java.util.List;

public interface Dao<T> {
    String defaultDataBaseUrl = "main.db";
    String fullConnectionUrl = "jdbc:sqlite:" + defaultDataBaseUrl;

    int execInsert(T insertValue) throws Exception;

    int execDelete(String KeyColumn) throws Exception;

    List<T> execQuery(String column, String value) throws Exception;

    int execUpdate(String column, String value, String KeyColumn) throws Exception;
}
