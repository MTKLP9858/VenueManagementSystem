package com.klp.vms.entity;

import lombok.Data;

import java.util.HashMap;

@Data
public class Stadium {//场馆
    private String name;//KEY
    private String address;//c
    private String introduction;//c
    private String contact;//c
    private String adminUserID;//c
    private User adminUser;
    private HashMap<String, Venue> venues;
}
