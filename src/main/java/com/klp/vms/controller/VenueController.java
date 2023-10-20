package com.klp.vms.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.klp.vms.entity.User;
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
import java.text.ParseException;

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
     * <p>uuid:场地的uuid</p>
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
        } catch (SQLException | ParseException e) {
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
     * 通过场地UUID来查询场地详细信息
     *
     * @param accessToken 应为管理员令牌
     * @param venueUUID   场地UUID
     * @return <p>返回带有多个变量的json对象</p>
     * <li>成功：
     * <p>success=true</p>
     * <p>uuid:场地的uuid 非必要信息，输入里有</p>
     * <p>name:场地名字</p>
     * <p>area:场地区域</p>
     * <p>stadium:所属场馆</p>
     * <p>introduction:球场介绍，可能为null</p>
     * <p>state:状态字符串，可能为："已开启","已关闭","待关闭"</p>
     * <p>price:价格，double值</p>
     * </li>
     * <li>失败：success=false</li>
     * <li>详细信息见message</li>
     */

    @PostMapping("/queryByUUID")
    public String queryByUUID(@RequestHeader String accessToken, @RequestParam String venueUUID) {
        Venue venue;
        try {
            if (UserService.verifyAccessToken(accessToken).getOp() == User.OP.ADMIN) {
                venue = VenueService.query(accessToken, venueUUID);
            } else {
                return new RuntimeError("Query by venueUUID? Permission denied! Please query by name,area,stadium.", 501).toString();
            }
        } catch (SQLException | ParseException e) {
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
     * 修改场地信息
     *
     * @param accessToken 应为管理员令牌
     * @param name        需要查询的场地名字，在相同的area和stadium下唯一
     * @param area        需要查询的场地所属区域
     * @param stadium     需要查询的场地所属场馆
     * @param column      需要更改的信息类型："name", "area", "stadium", "introduction", "price" ;只能从中选一
     * @param value       需要更改的值（在column列表中，除了price类型为double，其余均为String
     * @return <p>返回带有多个变量的json对象</p>
     * <li>成功：success=true</li>
     * <li>失败：success=false</li>
     * <li>详细信息见message</li>
     */
    @PostMapping("/update")
    public String update(@RequestHeader String accessToken, @RequestParam String name, @RequestParam String area, @RequestParam String stadium, @RequestParam String column, @RequestParam Object value) {
        try {
            VenueService.update(accessToken, VenueService.getUUID(name, area, stadium), column, value);
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
        json.put("code", 232);
        json.put("success", true);
        return json.toString();
    }

    /**
     * 启用场地，通过uuid来启用
     * （若场地已启用，则返回报错）
     *
     * @param accessToken 场地管理员令牌
     * @param uuid        场地UUID
     * @return <p>返回带有多个变量的json对象</p>
     * <li>成功：success=true</li>
     * <li>失败：success=false</li>
     * <li>详细信息见message</li>
     */
    @PostMapping("/openByUUID")
    public String openByUUID(@RequestHeader String accessToken, @RequestParam String uuid) {
        try {
            VenueService.verifyAdminOfVenueByUUID(accessToken, uuid);
            VenueService.open(uuid);
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
        json.put("code", 232);
        json.put("success", true);
        return json.toString();
    }

    /**
     * 关闭场地，通过uuid来停用场地，若还有订单未结算，则显示为待关闭
     *
     * @param accessToken 场地管理员令牌
     * @param uuid        场地UUID
     * @return <p>返回带有多个变量的json对象</p>
     * <li>成功：success=true</li>
     * <li>失败：success=false</li>
     * <li>详细信息见message</li>
     */
    @PostMapping("/closeByUUID")
    public String closeByUUID(@RequestHeader String accessToken, @RequestParam String uuid) {
        try {
            VenueService.verifyAdminOfVenueByUUID(accessToken, uuid);
            VenueService.close(uuid);
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
        json.put("code", 232);
        json.put("success", true);
        return json.toString();
    }

    /**
     * 启用场地，通过venueName, venueArea, stadium来启用
     * （若场地已启用，则返回报错）
     *
     * @param accessToken 场地管理员令牌
     * @param venueName   场地名字
     * @param venueArea   场地区域
     * @param stadium     场馆名字，用于验证
     * @return <p>返回带有多个变量的json对象</p>
     * <li>成功：success=true</li>
     * <li>失败：success=false</li>
     * <li>详细信息见message</li>
     */
    @PostMapping("/open")
    public String open(@RequestHeader String accessToken, @RequestParam String venueName, @RequestParam String venueArea, @RequestParam String stadium) {
        try {
            String venueUUID = VenueService.getUUID(venueName, venueArea, stadium);
            VenueService.verifyAdminOfVenueByUUID(accessToken, venueUUID);
            VenueService.open(venueUUID);
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
        json.put("code", 232);
        json.put("success", true);
        return json.toString();
    }

    /**
     * 关闭场地，通过venueName, venueArea, stadium来停用场地，若还有订单未结算，则显示为待关闭
     *
     * @param accessToken 场地管理员令牌
     * @param venueName   场地名字
     * @param venueArea   场地区域
     * @param stadium     场馆名字，用于验证
     * @return <p>返回带有多个变量的json对象</p>
     * <li>成功：success=true</li>
     * <li>失败：success=false</li>
     * <li>详细信息见message</li>
     */
    @PostMapping("/close")
    public String close(@RequestHeader String accessToken, @RequestParam String venueName, @RequestParam String venueArea, @RequestParam String stadium) {
        try {
            String venueUUID = VenueService.getUUID(venueName, venueArea, stadium);
            VenueService.verifyAdminOfVenueByUUID(accessToken, venueUUID);
            VenueService.close(venueUUID);
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
        json.put("code", 232);
        json.put("success", true);
        return json.toString();
    }


    /**
     * 拉取场地的图片
     *
     * @param accessToken 管理员令牌/用户令牌
     * @param name        场地名字
     * @param area        场地所属区域
     * @param stadium     场馆名字
     * @param imgIndex    图片的索引，如：0，1，2，3，4...  等，类型为int
     * @return <p>返回带有多个变量的json对象</p>
     * <li>成功：返回图片二进制</li>
     * <li>失败：success=false</li>
     * <li>详细信息见message</li>
     */
    @PostMapping("/queryImg")
    public ResponseEntity<byte[]> queryImg(@RequestHeader String accessToken, @RequestParam String name, @RequestParam String area, @RequestParam String stadium, @RequestParam int imgIndex) {
        try {
            byte[] img = VenueService.queryImg(accessToken, imgIndex, VenueService.getUUID(name, area, stadium));
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
     * 添加图片，总是在图片列表的最后添加
     *
     * @param accessToken 管理员令牌
     * @param venueName   场地名字
     * @param venueArea   场地区域
     * @param stadium     场馆名字，用于验证
     * @param img         MultipartFile图片文件
     * @return <p>返回带有多个变量的json对象</p>
     * <li>成功：success=true</li>
     * <li>失败：success=false</li>
     * <li>详细信息见message</li>
     */
    @PostMapping("/addImg")
    public String addImg(@RequestHeader String accessToken, @RequestParam String venueName, @RequestParam String venueArea, @RequestParam String stadium, @RequestParam MultipartFile img) {
        try {
            VenueService.addImg(accessToken, img, VenueService.getUUID(venueName, venueArea, stadium));
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
     * 按索引更新图片，将会替换旧图片
     *
     * @param accessToken 管理员令牌
     * @param venueName   场地名字
     * @param venueArea   场地区域
     * @param stadium     场馆名字，用于验证
     * @param img         MultipartFile图片文件
     * @param imgIndex    图片列表的索引，如：0，1，2，3，4。。。等，类型为int
     * @return <p>返回带有多个变量的json对象</p>
     * <li>成功：success=true</li>
     * <li>失败：success=false</li>
     * <li>详细信息见message</li>
     */
    @PostMapping("/updateImg")
    public String updateImg(@RequestHeader String accessToken, @RequestParam String venueName, @RequestParam String venueArea, @RequestParam String stadium, @RequestParam int imgIndex, @RequestParam MultipartFile img) {
        try {
            VenueService.updateImg(accessToken, imgIndex, img, VenueService.getUUID(venueName, venueArea, stadium));
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
     * @param venueName   场地名字
     * @param venueArea   场地区域
     * @param stadium     场馆名字，用于验证
     * @param imgIndex    图片列表的索引，如：0，1，2，3，4。。。等，类型为int
     * @return <p>返回带有多个变量的json对象</p>
     * <li>成功：success=true</li>
     * <li>失败：success=false</li>
     * <li>详细信息见message</li>
     */
    @PostMapping("/deleteImg")
    public String deleteImg(@RequestHeader String accessToken, @RequestParam String venueName, @RequestParam String venueArea, @RequestParam String stadium, @RequestParam int imgIndex) {
        try {
            VenueService.deleteImg(accessToken, imgIndex, VenueService.getUUID(venueName, venueArea, stadium));
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

}
