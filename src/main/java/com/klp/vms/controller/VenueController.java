package com.klp.vms.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.klp.vms.entity.User;
import com.klp.vms.entity.Venue;
import com.klp.vms.exception.RuntimeError;
import com.klp.vms.method.StringFilter;
import com.klp.vms.service.UserService;
import com.klp.vms.service.VenueService;
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

@Slf4j
@Controller
@RestController
@RequestMapping("/venue")
public class VenueController {
    /**
     * 新增场地（自动添加区域）
     *
     * @param accessToken 管理员用户的令牌
     *                    <p>name         新增场地的名字，在相同的area和stadium下唯一
     *                    <p>area         新增场地的所属区域
     *                    <p>stadium      新增场地的所属场馆
     *                    <p>price        价格，double类型，单位：元/小时
     *                    <p>introduction 场地介绍，可以为null
     * @return 返回带有多个变量的json对象
     * <li>成功：success=true</li>
     * <li>失败：success=false</li>
     * <li>详细信息见message</li>
     */
    @PostMapping("/add")
    public String add(@RequestHeader String accessToken, @RequestBody JSONObject jsonParam) {
        log.debug("add:" + jsonParam);
        String name = jsonParam.getString("name");
        String area = jsonParam.getString("area");
        String stadium = jsonParam.getString("stadium");
        double price = jsonParam.getDoubleValue("price");
        String introduction = jsonParam.getString("introduction");
        try {
            if (StringFilter.hasNull(new String[]{name, area, stadium})) {
                throw new RuntimeError("Incomplete parameter inputs!", 1501);
            }
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
     *                    <p>name        需要删除的场地名字，在相同的area和stadium下唯一
     *                    <p>area        需要删除的场地所属区域
     *                    <p>stadium     需要删除的场地所属场馆
     * @return 返回带有多个变量的json对象
     * <li>成功：success=true</li>
     * <li>失败：success=false</li>
     * <li>详细信息见message</li>
     */
    @PostMapping("/delete")
    public String delete(@RequestHeader String accessToken, @RequestBody JSONObject jsonParam) {
        log.debug("delete:" + jsonParam);
        String name = jsonParam.getString("name");
        String area = jsonParam.getString("area");
        String stadium = jsonParam.getString("stadium");
        try {
            if (StringFilter.hasNull(new String[]{name, area, stadium})) {
                throw new RuntimeError("Incomplete parameter inputs!", 1501);
            }
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
     *                    <p>name        需要查询的场地名字，在相同的area和stadium下唯一
     *                    <p>area        需要查询的场地所属区域
     *                    <p>stadium     需要查询的场地所属场馆
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
    public String query(@RequestHeader String accessToken, @RequestBody JSONObject jsonParam) {
        log.debug("query:" + jsonParam);
        String name = jsonParam.getString("name");
        String area = jsonParam.getString("area");
        String stadium = jsonParam.getString("stadium");
        Venue venue;
        try {
            if (StringFilter.hasNull(new String[]{name, area, stadium})) {
                throw new RuntimeError("Incomplete parameter inputs!", 1501);
            }
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
     *                    <p>venueUUID   场地UUID
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
    public String queryByUUID(@RequestHeader String accessToken, @RequestBody JSONObject jsonParam) {
        log.debug("queryByUUID:" + jsonParam);
        String venueUUID = jsonParam.getString("venueUUID");
        Venue venue;
        try {
            if (venueUUID == null) {
                throw new RuntimeError("Incomplete parameter inputs!", 1501);
            }

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
     *                    <p>name        需要查询的场地名字，在相同的area和stadium下唯一
     *                    <p>area        需要查询的场地所属区域
     *                    <p>stadium     需要查询的场地所属场馆
     *                    <p>column      需要更改的信息类型："name", "area", "stadium", "introduction", "price" ;只能从中选一
     *                    <p>value       需要更改的值（在column列表中，除了price类型为double，其余均为String
     * @return <p>返回带有多个变量的json对象</p>
     * <li>成功：success=true</li>
     * <li>失败：success=false</li>
     * <li>详细信息见message</li>
     */
    @PostMapping("/update")
    public String update(@RequestHeader String accessToken, @RequestBody JSONObject jsonParam) {
        log.debug("update:" + jsonParam);
        String name = jsonParam.getString("name");
        String area = jsonParam.getString("area");
        String stadium = jsonParam.getString("stadium");
        String column = jsonParam.getString("column");
        Object value = jsonParam.getDoubleValue("value");
        try {
            if (StringFilter.hasNull(new Object[]{name, area, stadium, column, value})) {
                throw new RuntimeError("Incomplete parameter inputs!", 1501);
            }
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
     *                    <p>uuid        场地UUID
     * @return <p>返回带有多个变量的json对象</p>
     * <li>成功：success=true</li>
     * <li>失败：success=false</li>
     * <li>详细信息见message</li>
     */
    @PostMapping("/openByUUID")
    public String openByUUID(@RequestHeader String accessToken, @RequestBody JSONObject jsonParam) {
        log.debug("openByUUID:" + jsonParam);
        String uuid = jsonParam.getString("uuid");
        try {
            if (uuid == null) {
                throw new RuntimeError("Incomplete parameter inputs!", 1501);
            }
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
     *                    <p>uuid        场地UUID
     * @return <p>返回带有多个变量的json对象</p>
     * <li>成功：success=true</li>
     * <li>失败：success=false</li>
     * <li>详细信息见message</li>
     */
    @PostMapping("/closeByUUID")
    public String closeByUUID(@RequestHeader String accessToken, @RequestBody JSONObject jsonParam) {
        log.debug("closeByUUID:" + jsonParam);
        String uuid = jsonParam.getString("uuid");
        try {
            if (uuid == null) {
                throw new RuntimeError("Incomplete parameter inputs!", 1501);
            }
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
     *                    <p>venueName   场地名字
     *                    <p>venueArea   场地区域
     *                    <p>stadium     场馆名字，用于验证
     * @return <p>返回带有多个变量的json对象</p>
     * <li>成功：success=true</li>
     * <li>失败：success=false</li>
     * <li>详细信息见message</li>
     */
    @PostMapping("/open")
    public String open(@RequestHeader String accessToken, @RequestBody JSONObject jsonParam) {
        log.debug("open:" + jsonParam);
        String venueName = jsonParam.getString("venueName");
        String venueArea = jsonParam.getString("venueArea");
        String stadium = jsonParam.getString("stadium");
        try {
            if (StringFilter.hasNull(new String[]{venueName, venueArea, stadium})) {
                throw new RuntimeError("Incomplete parameter inputs!", 1501);
            }
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
     *                    <p>venueName   场地名字
     *                    <p>venueArea   场地区域
     *                    <p>stadium     场馆名字，用于验证
     * @return <p>返回带有多个变量的json对象</p>
     * <li>成功：success=true</li>
     * <li>失败：success=false</li>
     * <li>详细信息见message</li>
     */
    @PostMapping("/close")
    public String close(@RequestHeader String accessToken, @RequestBody JSONObject jsonParam) {
        log.debug("close:" + jsonParam);
        String venueName = jsonParam.getString("venueName");
        String venueArea = jsonParam.getString("venueArea");
        String stadium = jsonParam.getString("stadium");
        try {
            if (StringFilter.hasNull(new String[]{venueName, venueArea, stadium})) {
                throw new RuntimeError("Incomplete parameter inputs!", 1501);
            }
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
     *                    <p>name        场地名字
     *                    <p>area        场地所属区域
     *                    <p>stadium     场馆名字
     *                    <p>imgIndex    图片的索引，如：0，1，2，3，4...  等，类型为int
     * @return <p>返回带有多个变量的json对象</p>
     * <li>成功：返回图片二进制</li>
     * <li>失败：success=false</li>
     * <li>详细信息见message</li>
     */
    @PostMapping("/queryImg")
    public ResponseEntity<byte[]> queryImg(@RequestHeader String accessToken, @RequestBody JSONObject jsonParam) {
        log.debug("queryImg:" + jsonParam);
        String name = jsonParam.getString("name");
        String area = jsonParam.getString("area");
        String stadium = jsonParam.getString("stadium");
        int imgIndex = jsonParam.getIntValue("imgIndex");
        try {
            if (StringFilter.hasNull(new String[]{name, area, stadium})) {
                throw new RuntimeError("Incomplete parameter inputs!", 1501);
            }
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
     * 添加图片，总是在图片列表的最后添加||需要提交的格式为表单(form)
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
     * 按索引更新图片，将会替换旧图片||需要提交的格式为表单(form)
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
     *                    <p>venueName   场地名字
     *                    <p>venueArea   场地区域
     *                    <p>stadium     场馆名字，用于验证
     *                    <p>imgIndex    图片列表的索引，如：0，1，2，3，4。。。等，类型为int
     * @return <p>返回带有多个变量的json对象</p>
     * <li>成功：success=true</li>
     * <li>失败：success=false</li>
     * <li>详细信息见message</li>
     */
    @PostMapping("/deleteImg")
    public String deleteImg(@RequestHeader String accessToken, @RequestBody JSONObject jsonParam) {
        log.debug("deleteImg:" + jsonParam);
        String venueName = jsonParam.getString("venueName");
        String venueArea = jsonParam.getString("venueArea");
        String stadium = jsonParam.getString("stadium");
        int imgIndex = jsonParam.getIntValue("imgIndex");
        try {
            if (StringFilter.hasNull(new String[]{venueName, venueArea, stadium})) {
                throw new RuntimeError("Incomplete parameter inputs!", 1501);
            }
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
