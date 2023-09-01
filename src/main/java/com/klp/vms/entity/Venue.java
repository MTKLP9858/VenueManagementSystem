package com.klp.vms.entity;

import lombok.Data;

@Data
public class Venue {//场地
    String name;
    String area;
    String stadium;//KEY
    String introduction;
    boolean active;
    double price;


    @Override
    public String toString() {
        return "{\"name\":\"" + name + "\"" + ", \"area\":\"" + area + "\"" + ", \"stadium\":\"" + stadium + "\"" + ", \"introduction\":\"" + introduction + "\"" + ", \"active\":" + active + ", \"price\":" + price + '}';
    }
}
