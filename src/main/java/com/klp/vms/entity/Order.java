package com.klp.vms.entity;

import lombok.Data;

import java.text.ParseException;
import java.text.SimpleDateFormat;

@Data
public class Order {
    public static class STATE {
        public final static String UNPAID = "未支付";//未支付过期被删除 timeout
        public final static String PAYING = "支付中";//支付中过期变未支付 timeout
        public final static String PAID = "已支付";//确认收款强制变为已支付，已支付进入占用时间变使用中
        public final static String USING = "使用中";//使用中超过占用时间变为已完成
        public final static String DONE = "已完成";//不会再发生变化，成为历史订单
        public final static String REFUNDING = "退款中";//只有已支付和使用中才能发起退款申请，转变为退款中
        public final static String REFUNDED = "已退款";//确认退款后从退款中转变为已退款，成为历史订单
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
        return "{\"number\":" + number + ", \"userid\":\"" + userid + "\"" + ", \"stadiumName\":\"" + stadiumName + "\"" + ", \"venueUUID\":\"" + venueUUID + "\"" + ", \"state\":\"" + state + "\"" + ", \"payTime\":" + payTimeLong + ", \"occupyStartTime\":" + startTime + ", \"occupyEndTime\":" + endTime + ", \"information\":" + (information == null ? "null" : "\"" + information + "\"") + ", \"message\":" + (message == null ? "null" : "\"" + message + "\"") + '}';
    }
}
