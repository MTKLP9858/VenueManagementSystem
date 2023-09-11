package com.klp.vms.entity;

import lombok.Data;

@Data
public class Venue {//场地

    public static class STATE {
        public final static String OPENED = "已开启";
        public final static String CLOSED = "已关闭";
        public final static String CLOSING = "待关闭";
    }

    String UUID;//KEY
    String name;
    String area;
    String stadium;
    String introduction;
    String state;
    double price;


    @Override
    public String toString() {
        return "{\"uuid\":\"" + UUID + "\"" + ", \"name\":\"" + name + "\"" + ", \"area\":\"" + area + "\"" + ", \"stadium\":\"" + stadium + "\"" + ", \"introduction\":" + (introduction == null ? null : ("\"" + introduction + "\"")) + ", \"state\":\"" + state + "\", \"price\":" + price + '}';
    }
}
