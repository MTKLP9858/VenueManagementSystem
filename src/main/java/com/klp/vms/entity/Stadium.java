package com.klp.vms.entity;

import lombok.Data;

import java.util.ArrayList;

@Data
public class Stadium {//场馆
    private String name;//KEY
    private String address;//c
    private String introduction;//c
    private String contact;//c
    private String adminUserID;//c
    private User adminUser;
    private ArrayList<Venue> venues;


    @Override
    public String toString() {
        return "{\"name\":\"" + name + "\"" +
                ", \"address\":\"" + address + "\"" +
                ", \"introduction\":\"" + introduction + "\"" +
                ", \"contact\":\"" + contact + "\"" +
                ", \"adminUserID\":\"" + adminUserID + "\"" +
                '}';
    }
}
