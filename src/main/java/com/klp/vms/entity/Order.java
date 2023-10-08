package com.klp.vms.entity;

import lombok.Data;

import java.text.ParseException;
import java.text.SimpleDateFormat;

@Data
public class Order {
    public static class STATE {
        public final static String UNPAID = "未支付";
        public final static String PAYING = "支付中";
        public final static String PAID = "已支付";
        public final static String USING = "使用中";
        public final static String DONE = "已完成";
        public final static String REFUNDING = "退款中";
        public final static String REFUNDED = "已退款";
    }

    private long number;//KEY
    private String userid;
    private String stadiumName;
    private String venueUUID;
    private String state;
    private String payTime;
    private String occupyStartTime;
    private String occupyEndTime;


    private String information = null;
    private String message = null;

    @Override
    public String toString() {
        long startTime;
        long endTime;
        long payTimeLong;
        try {
            payTimeLong = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(payTime).getTime();
            startTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(occupyStartTime).getTime();
            endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(occupyEndTime).getTime();
        } catch (ParseException | RuntimeException e) {
            return "This order is broken! " + e.getMessage();

        }
        return "{\"number\":" + number +
                ", \"userid\":\"" + userid + "\"" +
                ", \"stadiumName\":\"" + stadiumName + "\"" +
                ", \"venueUUID\":\"" + venueUUID + "\"" +
                ", \"state\":\"" + state + "\"" +
                ", \"payTime\":" + payTimeLong +
                ", \"occupyStartTime\":" + startTime +
                ", \"occupyEndTime\":" + endTime +
                ", \"information\":" + (information == null ? "null" : "\"" + information + "\"") +
                ", \"message\":" + (message == null ? "null" : "\"" + message + "\"") +
                '}';
    }
}
