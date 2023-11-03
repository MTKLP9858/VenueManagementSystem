package com.klp.vms.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.klp.vms.entity.Order;
import com.klp.vms.entity.Stadium;
import com.klp.vms.entity.Venue;
import com.klp.vms.exception.RuntimeError;
import com.klp.vms.method.StringFilter;
import com.klp.vms.service.StadiumService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

@Slf4j
@Controller
@RestController
@RequestMapping(value = "/stadium", produces = "application/json;;charset=UTF-8")
public class StadiumController {
    /**
     * 拉取场地的图片
     *
     * @param accessToken 管理员令牌/用户令牌
     *                    <p>stadiumName 场馆名字
     *                    <p>imgIndex[int]    图片的索引，如：0，1，2，3，4...
     * @return <p>返回带有多个变量的json对象</p>
     * <li>成功：返回图片二进制</li>
     * <li>失败：success=false</li>
     * <li>详细信息见message</li>
     */
    @PostMapping("/queryImg")
    public ResponseEntity<byte[]> queryImg(@RequestHeader String accessToken, @RequestBody JSONObject jsonParam) {
        log.debug("queryImg:" + jsonParam);
        int imgIndex = jsonParam.getIntValue("imgIndex");
        String stadiumName = jsonParam.getString("stadiumName");
        try {
            if (stadiumName == null) {
                throw new RuntimeError("Incomplete parameter inputs!", 1501);
            }

            byte[] img = StadiumService.queryImg(accessToken, imgIndex, stadiumName);
            return new ResponseEntity<>(img, HttpStatus.OK);
        } catch (RuntimeError e) {
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_HTML_VALUE);
            return new ResponseEntity<>(e.toString().getBytes(), headers, HttpStatus.NOT_FOUND);
        } catch (SQLException | ParseException e) {
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
     * 添加图片，总是在图片列表的最后添加||需要提交的格式为表单(form)
     *
     * @param accessToken 管理员令牌
     * @param stadiumName 场馆名字
     * @param img         MultipartFile图片文件
     * @return <p>返回带有多个变量的json对象</p>
     * <li>成功：success=true</li>
     * <li>失败：success=false</li>
     * <li>详细信息见message</li>
     */
    @PostMapping("/addImg")
    public String addImg(@RequestHeader String accessToken, @RequestParam String stadiumName, @RequestParam MultipartFile img) {
        log.debug("addImg:" + stadiumName);
        try {
            StadiumService.addImg(accessToken, img, stadiumName);
        } catch (SQLException | ParseException e) {
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

    /**
     * 按索引更新图片，将会替换旧图片||需要提交的格式为表单(form)
     *
     * @param accessToken 管理员令牌
     * @param stadiumName 场馆名字
     * @param img         MultipartFile图片文件
     * @param imgIndex    图片列表的索引，如：0，1，2，3，4。。。等，类型为int
     * @return <p>返回带有多个变量的json对象</p>
     * <li>成功：success=true</li>
     * <li>失败：success=false</li>
     * <li>详细信息见message</li>
     */
    @PostMapping("/updateImg")
    public String updateImg(@RequestHeader String accessToken, @RequestParam String stadiumName, @RequestParam int imgIndex, @RequestParam MultipartFile img) {
        log.debug("updateImg:" + stadiumName);
        try {
            StadiumService.updateImg(accessToken, imgIndex, img, stadiumName);
        } catch (SQLException | ParseException e) {
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

    /**
     * 按索引删除图片
     *
     * @param accessToken 管理员令牌
     *                    <p>stadiumName 场馆名字
     *                    <p>imgIndex    图片列表的索引，如：0，1，2，3，4。。。等，类型为int
     * @return <p>返回带有多个变量的json对象</p>
     * <li>成功：success=true</li>
     * <li>失败：success=false</li>
     * <li>详细信息见message</li>
     */
    @PostMapping("/deleteImg")
    public String deleteImg(@RequestHeader String accessToken, @RequestBody JSONObject jsonParam) {
        log.debug("deleteImg:" + jsonParam);
        int imgIndex = jsonParam.getIntValue("imgIndex");
        String stadiumName = jsonParam.getString("stadiumName");
        try {
            if (stadiumName == null) {
                throw new RuntimeError("Incomplete parameter inputs!", 1501);
            }
            StadiumService.deleteImg(accessToken, imgIndex, stadiumName);
        } catch (SQLException | ParseException e) {
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

    /**
     * 查询该场馆所有场地的所有订单（列表）
     *
     * @param accessToken 管理员令牌
     *                    <p>stadiumName 场馆名字
     * @return 返回一个包含order对象的jsonArray字符串，若返回0个订单，则应返回"[]" 。
     * 先用jsonArray解析可得到一些jsonObject，接着解析jsonObject可得到单个订单的信息。
     * 示例：
     * [{"number":1697701757615, "userid":"user1", "stadiumName":"stadium1", "venueUUID":"063fd8c6-097e-49a0-b2ea-93bded6b43d4", "state":"已完成", "payTime":1697701757000, "occupyStartTime":1697701757000, "occupyEndTime":1697702117000, "information":"infor111111111", "message":"message2222222222"},
     * {"number":1697702247822, "userid":"user1", "stadiumName":"stadium1", "venueUUID":"063fd8c6-097e-49a0-b2ea-93bded6b43d4", "state":"已完成", "payTime":1697702247000, "occupyStartTime":1697702247000, "occupyEndTime":1697702847000, "information":"infor111111111", "message":"message2222222222"},
     * {"number":1697702299674, "userid":"user1", "stadiumName":"stadium1", "venueUUID":"063fd8c6-097e-49a0-b2ea-93bded6b43d4", "state":"已完成", "payTime":1697702299000, "occupyStartTime":1697702899000, "occupyEndTime":1697703499000, "information":"infor111111111", "message":"message2222222222"},
     * {"number":1697771551167, "userid":"user1", "stadiumName":"stadium1", "venueUUID":"063fd8c6-097e-49a0-b2ea-93bded6b43d4", "state":"已完成", "payTime":1697771551000, "occupyStartTime":1697772150000, "occupyEndTime":1697772270000, "information":"infor111111111", "message":"message2222222222"},
     * {"number":1697772399387, "userid":"user1", "stadiumName":"stadium1", "venueUUID":"063fd8c6-097e-49a0-b2ea-93bded6b43d4", "state":"已支付", "payTime":1697772399000, "occupyStartTime":1697772999000, "occupyEndTime":1697773599000, "information":"infor111111111", "message":"message2222222222"}]
     */
    @PostMapping("/queryAllOrders")
    public String queryAllOrders(@RequestHeader String accessToken, @RequestBody JSONObject jsonParam) {
        log.debug("queryAllOrders:" + jsonParam);
        String stadiumName = jsonParam.getString("stadiumName");
        List<Order> orders;
        try {
            if (stadiumName == null) {
                throw new RuntimeError("Incomplete parameter inputs!", 1501);
            }
            orders = StadiumService.queryAllOrders(accessToken, stadiumName);
        } catch (SQLException | ParseException e) {
            JSONObject json = new JSONObject();
            json.put("code", 9);
            json.put("success", false);
            json.put("message", e.getMessage());
            return json.toString();
        } catch (RuntimeError e) {
            return e.toString();
        }
        JSONArray jsonArray = (JSONArray) JSONArray.parse(orders.toString());
        return jsonArray.toString();
    }


    /**
     * 查询该场馆所有场地的所有场馆（列表）
     *
     * @param accessToken 管理员令牌
     *                    <p>stadiumName 场馆名字
     * @return 返回一个包含order对象的jsonArray字符串，若返回0个订单，则应返回"[]" 。
     * 先用jsonArray解析可得到一些jsonObject，接着解析jsonObject可得到单个订单的信息。
     * 示例：
     * [{"area":"area1","price":30.5,"name":"venue1","stadium":"stadium1","state":"已开启","uuid":"62b9762d-dd99-4154-9094-1388d6423a6a","introduction":"inssss"},
     * {"area":"area1","price":20.0,"name":"venue2","stadium":"stadium1","state":"已开启","uuid":"063fd8c6-097e-49a0-b2ea-93bded6b43d4"},
     * {"area":"area2","price":30.5,"name":"venue1","stadium":"stadium1","state":"已开启","uuid":"6da6ca46-fb37-4fc9-a16f-c705e39748ef"}]
     */
    @PostMapping("/queryAllVenue")
    public String queryAllVenue(@RequestHeader String accessToken, @RequestBody JSONObject jsonParam) {
        log.debug("queryAllVenue:" + jsonParam);
        String stadiumName = jsonParam.getString("stadiumName");
        List<Venue> venues;
        try {
            if (stadiumName == null) {
                throw new RuntimeError("Incomplete parameter inputs!", 1501);
            }
            venues = StadiumService.queryAllVenue(accessToken, stadiumName);
        } catch (SQLException | ParseException e) {
            JSONObject json = new JSONObject();
            json.put("code", 9);
            json.put("success", false);
            json.put("message", e.getMessage());
            return json.toString();
        } catch (RuntimeError e) {
            return e.toString();
        }
        JSONArray jsonArray = (JSONArray) JSONArray.parse(venues.toString());
        return jsonArray.toString();
    }


    /**
     * 添加场馆（超级管理员）
     *
     * @param accessToken 超级管理员令牌
     *                    <p>stadiumName  新建的场馆名字
     *                    <p>address      新建的场馆地址
     *                    <p>introduction 新增的场馆介绍，可以为null
     *                    <p>contact      新建的场馆联系方式
     *                    <p>adminUserID  新建场馆的管理员id,需验证
     * @return <p>返回带有多个变量的json对象</p>
     * <li>成功：success=true</li>
     * <li>失败：success=false</li>
     * <li>详细信息见message</li>
     */
    @PostMapping("/add")
    public String add(@RequestHeader String accessToken, @RequestBody JSONObject jsonParam) {
        log.debug("add:" + jsonParam);
        String stadiumName = jsonParam.getString("stadiumName");
        String address = jsonParam.getString("address");
        String introduction = jsonParam.getString("introduction");
        String contact = jsonParam.getString("contact");
        String adminUserID = jsonParam.getString("adminUserID");
        try {
            if (StringFilter.hasNull(new String[]{stadiumName, address, contact, adminUserID})) {
                throw new RuntimeError("Incomplete parameter inputs!", 1501);
            }
            if (introduction == null) {
                introduction = "";
            }
            StadiumService.add(accessToken, stadiumName, address, introduction, contact, adminUserID);
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
        json.put("code", 1000);
        json.put("success", true);
        return json.toString();
    }

    /**
     * 删除场馆（超级管理员）
     *
     * @param accessToken 超级管理员令牌
     *                    <p>stadiumName 需要被删除的的场馆名字
     * @return <p>返回带有多个变量的json对象</p>
     * <li>成功：success=true</li>
     * <li>失败：success=false</li>
     * <li>详细信息见message</li>
     */
    @PostMapping("/delete")
    public String delete(@RequestHeader String accessToken, @RequestBody JSONObject jsonParam) {
        log.debug("delete:" + jsonParam);
        String stadiumName = jsonParam.getString("stadiumName");
        try {
            if (stadiumName == null) {
                throw new RuntimeError("Incomplete parameter inputs!", 1501);
            }
            StadiumService.delete(accessToken, stadiumName);
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
        json.put("code", 1000);
        json.put("success", true);
        return json.toString();
    }

    /**
     * 获取场馆信息
     *
     * @param accessToken 管理员令牌/用户令牌
     *                    <p>stadiumName 需要查找的场馆名字
     * @return <p>返回带有多个变量的json对象</p>
     * <li>
     * <p>成功：success=true</p>
     * <p>name:场馆名字</p>
     * <p>address:场馆地址</p>
     * <p>introduction:场馆介绍</p>
     * <p>contact:场馆联系方式</p>
     * <p>adminUserID:场馆管理员ID</p>
     * </li>
     * <li>失败：success=false</li>
     * <li>详细信息见message</li>
     */
    @PostMapping("/query")
    public String query(@RequestHeader String accessToken, @RequestBody JSONObject jsonParam) {
        log.debug("query:" + jsonParam);
        String stadiumName = jsonParam.getString("stadiumName");
        Stadium stadium;
        try {
            if (stadiumName == null) {
                throw new RuntimeError("Incomplete parameter inputs!", 1501);
            }
            stadium = StadiumService.query(accessToken, stadiumName);
        } catch (SQLException | ParseException e) {
            JSONObject json = new JSONObject();
            json.put("code", 9);
            json.put("success", false);
            json.put("message", e.getMessage());
            return json.toString();
        } catch (RuntimeError e) {
            return e.toString();
        }
        JSONObject json = JSONObject.parseObject(stadium.toString());
        json.put("code", 1000);
        json.put("success", true);
        return json.toString();
    }

    /**
     * 更改场馆信息
     *
     * @param accessToken 管理员令牌
     *                    <p>stadiumName 场馆名字
     *                    <p>column      需要更改的信息类型："name", "address", "introduction", "contact", "adminUserID";选择其中之一
     *                    <p>value       需要更改的值
     * @return <p>返回带有多个变量的json对象</p>
     * <li>成功：success=true</li>
     * <li>失败：success=false</li>
     * <li>详细信息见message</li>
     */
    @PostMapping("/update")
    public String update(@RequestHeader String accessToken, @RequestBody JSONObject jsonParam) {
        log.debug("update:" + jsonParam);
        String stadiumName = jsonParam.getString("stadiumName");
        String column = jsonParam.getString("column");
        String value = jsonParam.getString("value");
        try {
            if (StringFilter.hasNull(new String[]{stadiumName, column, value})) {
                throw new RuntimeError("Incomplete parameter inputs!", 1501);
            }
            StadiumService.update(accessToken, stadiumName, column, value);
        } catch (SQLException | ParseException e) {
            JSONObject json = new JSONObject();
            json.put("code", 9);
            json.put("success", false);
            json.put("message", e.getMessage());
            return json.toString();
        } catch (RuntimeError e) {
            return e.toString();
        }
        JSONObject json = new JSONObject();
        json.put("code", 1000);
        json.put("success", true);
        return json.toString();
    }

}
