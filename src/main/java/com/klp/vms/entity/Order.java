package com.klp.vms.entity;

import lombok.Data;

@Data
public class Order {
    public static class STATE {
        public final static String NOTPAID = "未支付";
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


    private String information;
    private String message;
}
