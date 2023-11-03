package com.klp.vms.controller;

import com.alibaba.fastjson.JSONObject;
import com.klp.vms.entity.User;
import com.klp.vms.exception.RuntimeError;
import com.klp.vms.method.StringFilter;
import com.klp.vms.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.sql.SQLException;
import java.util.HashMap;

@Slf4j
@Controller
@RestController
@RequestMapping(value = "/user", produces = "application/json;;charset=UTF-8")
public class UserController {

    /**
     * 更新或新建用户头像
     *
     * @param accessToken 位于请求头的用户令牌
     * @param img         MultipartFile图片文件
     * @return 返回带有多个变量的json对象
     * <li>成功：success=true</li>
     * <li>失败：success=false</li>
     * <li>详细信息见message</li>
     */
    @PostMapping("/updateAvatar")
    public String updateAvatar(@RequestHeader String accessToken, @RequestParam MultipartFile img) {
        log.debug("updateAvatar : accessToken=" + accessToken);
        try {
            UserService.updateAvatar(accessToken, img);
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
        json.put("message", "update avatar success");
        return json.toString();
    }

    /**
     * 拉取用户头像
     *
     * @param accessToken 位于请求头的用户令牌
     * @return 若成功则返回图片的二进制流，可于请求头Accept指定："image/png","image/jpeg","image/gif"
     * <li>成功：图片二进制</li>
     * <li>失败：success=false   请参阅返回json中的message</li>
     */
    @PostMapping(value = "/queryAvatar", produces = {MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_GIF_VALUE})
    public ResponseEntity<byte[]> queryAvatar(@RequestHeader String accessToken) {
        log.debug("queryAvatar : accessToken=" + accessToken);
        try {
            return new ResponseEntity<>(UserService.queryAvatar(accessToken), HttpStatus.OK);
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

    /**
     * 更新用户昵称
     *
     * @param accessToken 位于请求头的用户令牌
     *                    <p>newUsername 用户的新昵称
     * @return 返回带有多个变量的json对象
     * <li>成功：success=true</li>
     * <li>失败：success=false</li>
     * <li>详细信息见message</li>
     */
    @PostMapping("/rename")
    public String rename(@RequestHeader String accessToken, @RequestBody JSONObject jsonParam) {
        String newUsername = jsonParam.getString("newUsername");
        try {
            if (newUsername == null) {
                throw new RuntimeError("Incomplete parameter inputs!", 1501);
            }
            UserService.rename(newUsername, accessToken);
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
        json.put("message", "rename success");
        return json.toString();
    }

    /**
     * <li>若accessToken过期，则使用此接口刷新accessToken</li>
     * <li>若refreshToken即将过期，则在自动刷新期限内返回新的refreshToken</li>
     * <li>若refreshToken过期，则需要重新登录</li>
     *
     * @param refreshToken 用户的刷新令牌
     * @return <p>返回带有多个变量的json对象</p>
     * <li>成功：
     * <p>success=true</p>
     * <p>accessToken:更新后的用户令牌</p>
     * <p>refreshToken:更新后的用户刷新令牌（若有）</p>
     * </li>
     * <li>失败：success=false</li>
     * <li>详细信息见message</li>
     */
    @PostMapping("/refresh")
    public String refresh(@RequestHeader String refreshToken) {
        log.debug("refresh : refreshToken=" + refreshToken);
        HashMap<String, String> map;
        try {
            map = UserService.doRefreshToken(refreshToken);
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
        json.put("accessToken", map.get("access_token"));
        if (map.containsKey("refreshToken")) {
            json.put("refreshToken", map.get("refresh_token"));
        }
        json.put("code", 200);
        json.put("success", true);
        json.put("message", "refresh success");
        return json.toString();
    }

    /**
     * 为用户（顾客）注册
     *
     * @param jsonParam json传参
     *                  <p>userid   用户id：应为手机号（没有验证机制）
     *                  <p>password 用户密码
     *                  <p>username 用户昵称
     * @return <p>返回带有多个变量的json对象</p>
     * <li>成功：
     * <p>success=true</p>
     * <p>userid:用户id：应为手机号</p>
     * <p>username:用户昵称</p>
     * <p>op:0</p>
     * <p>accessToken:用户令牌</p>
     * <p>accessTokenAge:用户令牌有效截至日期</p>
     * <p>refreshToken:用户刷新令牌</p>
     * <p>refreshTokenAge:用户刷新令牌有效截至日期</p>
     * </li>
     * <li>失败：success=false</li>
     * <li>详细信息见message</li>
     */
    @PostMapping("/register")
    public String register(@RequestBody JSONObject jsonParam) {
        log.debug("register:" + jsonParam);
        String userid = jsonParam.getString("userid");
        String username = jsonParam.getString("username");
        String password = jsonParam.getString("password");
        User user;
        try {
            if (StringFilter.hasNull(new String[]{userid, password})) {
                throw new RuntimeError("Incomplete parameter inputs!", 1501);
            }
            if (username == null) {
                username = "User" + userid;
            }
            user = UserService.register(userid, username, password, 0);
        } catch (SQLException e) {
            JSONObject json = new JSONObject();
            json.put("code", 9);
            json.put("success", false);
            json.put("message", e.getMessage());
            return json.toString();
        } catch (RuntimeError e) {
            return e.toString();
        }
        JSONObject json = (JSONObject) JSONObject.parse(user.toString());
        json.put("code", 200);
        json.put("success", true);
        json.put("message", "register success");
        return json.toString();
    }

    /**
     * 为用户（场地管理员）注册
     *
     * @param accessToken 超级管理员用户令牌
     * @param jsonParam   json传参
     *                    <p>userid   用户id：应为手机号（没有验证机制）
     *                    <p>password 用户密码
     *                    <p>username 用户昵称
     * @return <p>返回带有多个变量的json对象</p>
     * <li>成功：
     * <p>success=true</p>
     * <p>userid:用户id：应为手机号</p>
     * <p>username:用户昵称</p>
     * <p>op:5</p>
     * <p>accessToken:用户令牌</p>
     * <p>accessTokenAge:用户令牌有效截至日期</p>
     * <p>refreshToken:用户刷新令牌</p>
     * <p>refreshTokenAge:用户刷新令牌有效截至日期</p>
     * </li>
     * <li>失败：success=false</li>
     * <li>详细信息见message</li>
     */
    @PostMapping("/registerAdmin")
    public String registerAdmin(@RequestHeader String accessToken, @RequestBody JSONObject jsonParam) {
        log.debug("registerAdmin:" + jsonParam);
        String userid = jsonParam.getString("userid");
        String username = jsonParam.getString("username");
        String password = jsonParam.getString("password");
        User user;
        try {
            if (StringFilter.hasNull(new String[]{userid, password})) {
                throw new RuntimeError("Incomplete parameter inputs!", 1501);
            }
            if (username == null) {
                username = "Admin" + userid;
            }
            User user1 = UserService.verifyAccessToken(accessToken);
            if (user1.getOp() == User.OP.SU) {
                user = UserService.register(userid, username, password, 5);
            } else {
                throw new RuntimeError("You are not a SU!", 1103);
            }
        } catch (SQLException e) {
            JSONObject json = new JSONObject();
            json.put("code", 9);
            json.put("success", false);
            json.put("message", e.getMessage());
            return json.toString();
        } catch (RuntimeError e) {
            return e.toString();
        }
        JSONObject json = (JSONObject) JSONObject.parse(user.toString());
        json.put("code", 200);
        json.put("success", true);
        json.put("message", "register success");
        return json.toString();
    }

    /**
     * 为用户登录并获取其accessToken等信息
     *
     * @param jsonParam json传参
     *                  <P>userid 用户id/唯一标识
     *                  <P>password 用户密码
     * @return <p>返回带有多个变量的json对象</p>
     * <li>成功：
     * <p>success=true</p>
     * <p>userid:用户id：应为手机号</p>
     * <p>username:用户昵称</p>
     * <p>op:权限等级</p>
     * <p>accessToken:用户令牌</p>
     * <p>accessTokenAge:用户令牌有效截至日期</p>
     * <p>refreshToken:用户刷新令牌</p>
     * <p>refreshTokenAge:用户刷新令牌有效截至日期</p>
     * </li>
     * <li>失败：success=false</li>
     * <li>详细信息见message</li>
     */
    @PostMapping("/login")
    public String login(@RequestBody JSONObject jsonParam) {
        log.debug("login:" + jsonParam);
        String userid = jsonParam.getString("userid");
        String password = jsonParam.getString("password");
        User user;
        try {
            if (StringFilter.hasNull(new String[]{userid, password})) {
                throw new RuntimeError("Incomplete parameter inputs!", 1501);
            }

            user = UserService.login(userid, password);
        } catch (SQLException e) {
            JSONObject json = new JSONObject();
            json.put("code", 9);
            json.put("success", false);
            json.put("message", e.getMessage());
            return json.toString();
        } catch (RuntimeError e) {
            return e.toString();
        }
        log.info("login:" + userid + " pwd:" + password);
        JSONObject json = (JSONObject) JSONObject.parse(user.toString());
        json.put("code", 200);
        json.put("success", true);
        json.put("message", "login success");
        return json.toString();
    }

    /**
     * 更改用户密码，这将需要重新登录
     *
     * @param accessToken 位于请求头的用户令牌
     *                    <p>oldPassword 需要验证的旧密码
     *                    <p>newPassword 需要更新的新密码
     * @return <p>返回带有多个变量的json对象</p>
     * <li>成功：success=true</li>
     * <li>失败：success=false</li>
     * <li>详细信息见message</li>
     */
    @PostMapping("/changePassword")
    public String changePassword(@RequestHeader String accessToken, @RequestBody JSONObject jsonParam) {
        log.debug("changePassword:" + jsonParam);
        String oldPassword = jsonParam.getString("oldPassword");
        String newPassword = jsonParam.getString("newPassword");
        try {
            if (StringFilter.hasNull(new String[]{oldPassword, newPassword})) {
                throw new RuntimeError("Incomplete parameter inputs!", 1501);
            }
            UserService.changePassword(accessToken, oldPassword, newPassword);
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
        json.put("message", "change password success");
        return json.toString();
    }


}
