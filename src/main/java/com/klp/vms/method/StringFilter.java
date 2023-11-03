package com.klp.vms.method;

public class StringFilter {
    public static boolean hasNull(Object[] strings) {
        for (Object s : strings) {
            if (s == null) return true;
        }
        return false;
    }
}
