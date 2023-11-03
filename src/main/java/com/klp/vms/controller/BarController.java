package com.klp.vms.controller;

import com.alibaba.fastjson.JSONObject;
import com.klp.vms.dao.BarDao;
import com.klp.vms.entity.User;
import com.klp.vms.exception.RuntimeError;
import com.klp.vms.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

/**
 * 公告栏类，需要accessToken的权限为SU（10）；
 * 参数index[int],msg[String]
 */

@Slf4j//TODO
@Controller
@RestController
@RequestMapping("/bar")
public class BarController {
    @PostMapping("/add")
    public String add(@RequestHeader String accessToken, @RequestParam("msg") String msg) {

        try {
            if (UserService.verifyAccessToken(accessToken).getOp() != User.OP.SU) {
                throw new RuntimeError("you are not a super admin!", 666);
            }
            new BarDao().execInsert(msg);
        } catch (SQLException e) {
            JSONObject json = new JSONObject();
            json.put("code", 9);
            json.put("success", false);
            json.put("message", e.getMessage());
            return json.toString();
        } catch (RuntimeError e) {
            return e.toString();
        }
        JSONObject json = new JSONObject();
        json.put("code", 200);
        json.put("success", true);
        json.put("message", "Add message succeed!");
        return json.toString();
    }

    @PostMapping("/delete")
    public String detele(@RequestHeader String accessToken, @RequestParam("index") int index) {
        try {
            if (UserService.verifyAccessToken(accessToken).getOp() != User.OP.SU) {
                throw new RuntimeError("you are not a super admin!", 666);
            }
            new BarDao().execDelete(index);
        } catch (SQLException e) {
            JSONObject json = new JSONObject();
            json.put("code", 9);
            json.put("success", false);
            json.put("message", e.getMessage());
            return json.toString();
        } catch (RuntimeError e) {
            return e.toString();
        }
        JSONObject json = new JSONObject();
        json.put("code", 200);
        json.put("success", true);
        json.put("message", "Delete message succeed!");
        return json.toString();
    }


    @PostMapping("/query")
    public String query(@RequestHeader String accessToken) {
        JSONObject json;
        try {
            if (UserService.verifyAccessToken(accessToken).getOp() != User.OP.SU) {
                throw new RuntimeError("you are not a super admin!", 666);
            }
            json = new BarDao().execQuery();
        } catch (SQLException e) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("code", 9);
            jsonObject.put("success", false);
            jsonObject.put("message", e.getMessage());
            return jsonObject.toString();
        } catch (RuntimeError e) {
            return e.toString();
        }
        json.put("code", 200);
        json.put("success", true);
        json.put("message", "Query message succeed!");
        return json.toString();
    }


    @PostMapping("/update")
    public String update(@RequestHeader String accessToken, @RequestParam("index") int index, @RequestParam("msg") String msg) {
        try {
            if (UserService.verifyAccessToken(accessToken).getOp() != User.OP.SU) {
                throw new RuntimeError("you are not a super admin!", 666);
            }
            new BarDao().execUpdate(index, msg);
        } catch (SQLException e) {
            JSONObject json = new JSONObject();
            json.put("code", 9);
            json.put("success", false);
            json.put("message", e.getMessage());
            return json.toString();
        } catch (RuntimeError e) {
            return e.toString();
        }
        JSONObject json = new JSONObject();
        json.put("code", 200);
        json.put("success", true);
        json.put("message", "Update message succeed!");
        return json.toString();
    }
}