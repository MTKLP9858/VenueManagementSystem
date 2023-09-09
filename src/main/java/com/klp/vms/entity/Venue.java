package com.klp.vms.entity;

import lombok.Data;

@Data
public class Venue {//场地
    String uuid;//KEY
    String name;
    String area;
    String stadium;
    String introduction;
    boolean active;
    double price;


    @Override
    public String toString() {
        return "{\"uuid\":\"" + uuid + "\"" + ", \"name\":\"" + name + "\"" + ", \"area\":\"" + area + "\"" + ", \"stadium\":\"" + stadium + "\"" + ", \"introduction\":\"" + introduction + "\"" + ", \"active\":" + active + ", \"price\":" + price + '}';
    }
}
