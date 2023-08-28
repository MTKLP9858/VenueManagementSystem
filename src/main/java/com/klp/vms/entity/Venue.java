package com.klp.vms.entity;

import lombok.Data;

@Data
public class Venue {//场地
    public String name;//KEY
    public String area;
    public String stadium;
    private String introduction;
    private boolean active;
    private double price;


}
