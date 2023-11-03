package com.klp.vms.method;

public class StringFilter {
    public static boolean hasNull(String[] strings) {
        for (String s : strings) {
            if (s == null) return true;
        }
        return false;
    }
}
