package com.klp.vms.entity;

import lombok.Data;

@Data
public class Order {
    private long number;//KEY
    private String userid;
    private String stadiumName;
    private String venueUUID;
    private String state;
    private String payTime;
    private String occupyStartTime;
    private String occupyEndTime;


    private String information;
    private String message;
}
