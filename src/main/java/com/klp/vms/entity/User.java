package com.klp.vms.entity;

import lombok.Data;

@Data
public class User {
    public User(int op) {
        this.op = op;
    }

    private String userid;//KEY,用户唯一id
    private String username;//用户昵称
    private String password;//密码
    /**
     * 0=顾客,5=球场管理,10=超级用户
     */
    private int op;//权限
    private String access_token;//访问token
    private String access_token_age;
    private String refresh_token;//更新token
    private String refresh_token_age;


    @Override
    public String toString() {
        return "{\"userid\":\"" + userid + "\"" +
                ", \"username\":\"" + username + "\"" +
                ", \"op\":" + op +
                ", \"accessToken\":\"" + access_token + "\"" +
                ", \"accessTokenAge\":\"" + access_token_age + "\"" +
                ", \"refreshToken\":\"" + refresh_token + "\"" +
                ", \"refreshTokenAge\":\"" + refresh_token_age + "\"" +
                '}';
    }
}
