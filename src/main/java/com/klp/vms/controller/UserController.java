package com.klp.vms.controller;

import com.alibaba.fastjson.JSONObject;
import com.klp.vms.entity.User;
import com.klp.vms.exception.RuntimeError;
import com.klp.vms.service.UserService;
import jakarta.annotation.Nullable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.sql.SQLException;
import java.util.HashMap;

@Controller
@RestController
@RequestMapping("/user")
public class UserController {
    @PostMapping("/update-avatar")
    public String updateAvatar(@RequestParam String access_token, @RequestParam MultipartFile img) {
        try {
            UserService.updateAvatar(access_token, img);
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
        json.put("code", 211);
        json.put("success", true);
        json.put("message", "update avatar success");
        return json.toString();
    }

    @PostMapping(value = "/query-avatar", produces = {MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_GIF_VALUE})
    public ResponseEntity<byte[]> queryAvatar(@RequestParam String access_token) {
        try {
            HttpHeaders headers = new HttpHeaders();
            return new ResponseEntity<>(UserService.queryAvatar(access_token), HttpStatus.OK);
        } catch (RuntimeError e) {
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_HTML_VALUE);
            JSONObject json = new JSONObject();
            json.put("code", e.getCode());
            json.put("success", false);
            json.put("message", e.getMessage());
            return new ResponseEntity<>(json.toString().getBytes(), headers, HttpStatus.NOT_FOUND);
        } catch (SQLException e) {
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_HTML_VALUE);
            JSONObject json = new JSONObject();
            json.put("code", 9);
            json.put("success", false);
            json.put("message", e.getMessage());
            return new ResponseEntity<>(json.toString().getBytes(), headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping("/rename")
    public String rename(@RequestParam String new_username, @RequestParam String access_token) {
        try {
            UserService.rename(new_username, access_token);
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
        json.put("code", 205);
        json.put("success", true);
        json.put("message", "rename success");
        return json.toString();
    }


    @PostMapping("/refresh")
    public String refresh(@RequestParam String refresh_token) {
        HashMap<String, String> map;
        try {
            map = UserService.doRefreshToken(refresh_token);
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
