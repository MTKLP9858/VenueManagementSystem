package com.klp.vms.entity;

import lombok.Data;

import java.util.HashMap;

@Data
public class Stadium {//场馆
    private String name;//KEY
    private String address;
    private String introduction;
    private String contact;
    private String userid;
    private User adminUser;
    private HashMap<String, Venue> venues;
}
