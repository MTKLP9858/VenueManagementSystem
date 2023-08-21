package com.klp.vms.entity;

import lombok.Data;

@Data
public class User {
    public User(int op) {
        this.op = op;
    }

    private String userid;//KEY
    private String username;
    private String password;
    /**
     * 0=顾客,5=球场管理,10=超级用户
     */
    private int op;
    private String access_token;
    private String access_token_age;
    private String refresh_token;
    private String refresh_token_age;


    @Override
    public String toString() {
        return "{\"userid\":\"" + userid + "\"" +
                ", \"username\":\"" + username + "\"" +
                ", \"op\":" + op +
                ", \"access_token\":\"" + access_token + "\"" +
                ", \"access_token_age\":\"" + access_token_age + "\"" +
                ", \"refresh_token\":\"" + refresh_token + "\"" +
                ", \"refresh_token_age\":\"" + refresh_token_age + "\"" +
                '}';
    }
}
