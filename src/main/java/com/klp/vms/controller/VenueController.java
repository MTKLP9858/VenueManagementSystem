package com.klp.vms.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.klp.vms.entity.Venue;
import com.klp.vms.exception.RuntimeError;
import com.klp.vms.service.UserService;
import com.klp.vms.service.VenueService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.sql.SQLException;

@Controller
@RestController
@RequestMapping("/venue")
public class VenueController {
    /**
     * 新增场地（自动添加区域）
     *
     * @param accessToken  管理员用户的令牌
     * @param name         新增场地的名字，在相同的area和stadium下唯一
     * @param area         新增场地的所属区域
     * @param stadium      新增场地的所属场馆
     * @param price        价格，double类型，单位：元/小时
     * @param introduction 场地介绍，不必须
     * @return 返回带有多个变量的json对象
     * <li>成功：success=true</li>
     * <li>失败：success=false</li>
     * <li>详细信息见message</li>
     */
    @PostMapping("/add")
    public String add(@RequestHeader String accessToken, @RequestParam String name, @RequestParam String area, @RequestParam String stadium, @RequestParam double price, @RequestParam(required = false) String introduction) {
        try {
            VenueService.add(accessToken, name, area, stadium, price, introduction);
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
        json.put("code", 213);
        json.put("success", true);
        json.put("message", "add venue success!");
        return json.toString();
    }

    /**
     * 删除场地，前提是场地已经被关闭
     *
     * @param accessToken 管理员用户的令牌
     * @param name        需要删除的场地名字，在相同的area和stadium下唯一
     * @param area        需要删除的场地所属区域
     * @param stadium     需要删除的场地所属场馆
     * @return 返回带有多个变量的json对象
     * <li>成功：success=true</li>
     * <li>失败：success=false</li>
     * <li>详细信息见message</li>
     */
    @PostMapping("/delete")
    public String delete(@RequestHeader String accessToken, @RequestParam String name, @RequestParam String area, @RequestParam String stadium) {
        try {
            VenueService.delete(accessToken, VenueService.getUUID(name, area, stadium));
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
        json.put("code", 321);
        json.put("success", true);
        json.put("message", "delete venue success!");
        return json.toString();
    }

    /**
     * 查询场地，查询场地的详细信息
     *
     * @param accessToken 管理员用户的令牌
     * @param name        需要查询的场地名字，在相同的area和stadium下唯一
     * @param area        需要查询的场地所属区域
     * @param stadium     需要查询的场地所属场馆
     * @return <p>返回带有多个变量的json对象</p>
     * <li>成功：
     * <p>success=true</p>
     * <p>uuid:场地的uuid 非必要信息，输入里有</p>
     * <p>name:场地名字 非必要信息，输入里有</p>
     * <p>area:场地区域 非必要信息，输入里有</p>
     * <p>stadium:所属场馆 非必要信息，输入里有</p>
     * <p>introduction:球场介绍，可能为null</p>
     * <p>state:状态字符串，可能为："已开启","已关闭","待关闭"</p>
     * <p>price:价格，double值</p>
     * </li>
     * <li>失败：success=false</li>
     * <li>详细信息见message</li>
     */
    @PostMapping("/query")
    public String query(@RequestHeader String accessToken, @RequestParam String name, @RequestParam String area, @RequestParam String stadium) {
        Venue venue;
        try {
            venue = VenueService.query(accessToken, VenueService.getUUID(name, area, stadium));
        } catch (SQLException e) {
            JSONObject json = new JSONObject();
            json.put("code", 9);
            json.put("success", false);
            json.put("message", e.getMessage());
            return json.toString();
        } catch (RuntimeError e) {
            return e.toString();
        }
        JSONObject json = JSON.parseObject(venue.toString());
        json.put("code", 432);
        json.put("success", true);
        return json.toString();
    }

    /**
     *
     * @param accessToken
     * @param name
     * @param area
     * @param stadium
     * @param column
     * @param value
     * @return
     */
    @PostMapping("/update")
    public String update(@RequestHeader String accessToken, @RequestParam String name, @RequestParam String area, @RequestParam String stadium, @RequestParam String column, @RequestParam String value) {
        try {
            VenueService.update(accessToken, VenueService.getUUID(name, area, stadium), column, value);
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
        json.put("code", 232);
        json.put("success", true);
        return json.toString();
    }

    @PostMapping("/queryImg")
    public ResponseEntity<byte[]> queryImg(@RequestHeader String accessToken, @RequestParam String name, @RequestParam String area, @RequestParam String stadium, @RequestParam int imgIndex) {
        try {
            byte[] img = VenueService.queryImg(accessToken, imgIndex, VenueService.getUUID(name, area, stadium));
            return new ResponseEntity<>(img, HttpStatus.OK);
        } catch (RuntimeError e) {
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_HTML_VALUE);
            return new ResponseEntity<>(e.toString().getBytes(), headers, HttpStatus.NOT_FOUND);
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

    @PostMapping("/addImg")
    public String addImg(@RequestHeader String accessToken, @RequestParam String name, @RequestParam String area, @RequestParam String stadium, @RequestParam MultipartFile img) {
        try {
            VenueService.addImg(accessToken, img, VenueService.getUUID(name, area, stadium));
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
        json.put("code", 324);
        json.put("success", true);
        return json.toString();
    }

    @PostMapping("/updateImg")
    public String updateImg(@RequestHeader String accessToken, @RequestParam String name, @RequestParam String area, @RequestParam String stadium, @RequestParam int imgIndex, @RequestParam MultipartFile img) {
        try {
            VenueService.updateImg(accessToken, imgIndex, img, VenueService.getUUID(name, area, stadium));
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
        json.put("code", 432);
        json.put("success", true);
        return json.toString();
    }

    @PostMapping("/deleteImg")
    public String deleteImg(@RequestHeader String accessToken, @RequestParam String name, @RequestParam String area, @RequestParam String stadium, @RequestParam int imgIndex) {
        try {
            VenueService.deleteImg(accessToken, imgIndex, VenueService.getUUID(name, area, stadium));
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
        json.put("code", 414);
        json.put("success", true);
        return json.toString();
    }

}
