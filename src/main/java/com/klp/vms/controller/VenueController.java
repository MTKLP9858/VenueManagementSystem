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
     * @param accessToken  an access token of admin or su
     * @param name         venue name
     * @param area         the area of this venue
     * @param stadium      the stadium of this venue
     * @param price        a number
     * @param introduction not required, an introduction
     * @return a {@link String} serialization by {@link Venue}
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
