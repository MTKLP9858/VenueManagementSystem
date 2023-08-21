package com.klp.vms.controller;

import com.alibaba.fastjson.JSONObject;
import com.klp.vms.entity.User;
import com.klp.vms.exception.RuntimeError;
import com.klp.vms.service.UserService;
import jakarta.annotation.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.HashMap;

@Controller
@RestController
@RequestMapping("/user")
public class UserController {
    @PostMapping("/refresh")
    public String register(@RequestParam String token) {
        HashMap<String, String> map;
        try {
            map = UserService.doRefreshToken(token);
        } catch (SQLException e) {
            JSONObject json = new JSONObject();
            json.put("code", 9);
            json.put("success", false);
            json.put("message", e.getMessage());
            return json.toString();
        } catch (RuntimeError e) {
            JSONObject json = new JSONObject();
            json.put("code", e.getCode());
            json.put("success", false);
            json.put("message", e.getMessage());
            return json.toString();
        }
        JSONObject json = new JSONObject();
        json.put("access_token", map.get("access_token"));
        json.put("refresh_token", map.get("refresh_token"));
        json.put("code", 203);
        json.put("success", true);
        json.put("message", "refresh success");
        return json.toString();
    }

    @PostMapping("/register")
    public String register(@RequestParam String userid, @RequestParam String password, @RequestParam(required = false) String username) {
        System.out.println("register:" + userid + " pwd:" + password);
        User user;
        if (username == null) {
            username = "User" + userid;
        }
        try {
            user = UserService.register(userid, username, password, 0);
        } catch (SQLException e) {
            JSONObject json = new JSONObject();
            json.put("code", 9);
            json.put("success", false);
            json.put("message", e.getMessage());
            return json.toString();
        } catch (RuntimeError e) {
            JSONObject json = new JSONObject();
            json.put("code", e.getCode());
            json.put("success", false);
            json.put("message", e.getMessage());
            return json.toString();
        }
        JSONObject json = (JSONObject) JSONObject.parse(user.toString());
        json.put("code", 201);
        json.put("success", true);
        json.put("message", "register success");
        return json.toString();
    }

    @PostMapping("/register-admin")
    public String registerAdmin(@RequestParam String userid, @RequestParam String password, @RequestParam @Nullable String username) {
        System.out.println("register:" + userid + " pwd:" + password);
        User user;
        if (username == null) {
            username = "Admin" + userid;
        }
        try {
            user = UserService.register(userid, username, password, 5);
        } catch (SQLException e) {
            JSONObject json = new JSONObject();
            json.put("code", 9);
            json.put("success", false);
            json.put("message", e.getMessage());
            return json.toString();
        } catch (RuntimeError e) {
            JSONObject json = new JSONObject();
            json.put("code", e.getCode());
            json.put("success", false);
            json.put("message", e.getMessage());
            return json.toString();
        }
        JSONObject json = (JSONObject) JSONObject.parse(user.toString());
        json.put("code", 201);
        json.put("success", true);
        json.put("message", "register success");
        return json.toString();
    }

    @PostMapping("/login")
    public String login(@RequestParam String userid, @RequestParam String password) {
        System.out.println("login:" + userid + " pwd:" + password);
        User user;
        try {
            user = UserService.login(userid, password);
        } catch (SQLException e) {
            JSONObject json = new JSONObject();
            json.put("code", 9);
            json.put("success", false);
            json.put("message", e.getMessage());
            return json.toString();
        } catch (RuntimeError e) {
            JSONObject json = new JSONObject();
            json.put("code", e.getCode());
            json.put("success", false);
            json.put("message", e.getMessage());
            return json.toString();
        }
        JSONObject json = (JSONObject) JSONObject.parse(user.toString());
        json.put("code", 200);
        json.put("success", true);
        json.put("message", "login success");
        return json.toString();
    }
}
