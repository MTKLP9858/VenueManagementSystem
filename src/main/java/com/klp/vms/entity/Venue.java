package com.klp.vms.entity;

import lombok.Data;

@Data
public class Venue {//场地
    public String name;
    public String area;
    public String stadium;//KEY
    private String introduction;
    private boolean active;
    private double price;


    @Override
    public String toString() {
        return "{\"name\":\"" + name + "\"" + ", \"area\":\"" + area + "\"" + ", \"stadium\":\"" + stadium + "\"" + ", \"introduction\":\"" + introduction + "\"" + ", \"active\":" + active + ", \"price\":" + price + '}';
    }
}
