package com.klp.vms.controller;

import com.klp.vms.exception.RuntimeError;
import com.klp.vms.service.ImageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j//TODO
@Controller
@RestController
@RequestMapping("/img")
public class ImageController {
    @PostMapping("/add")
    public String add(@RequestParam("img") MultipartFile img) {
        try {
            return ImageService.add(img);
        } catch (RuntimeError e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping(value = "/query", produces = {MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_GIF_VALUE})
    public byte[] query(@RequestParam("index") String index) {
        try {
            return ImageService.query(index);
        } catch (RuntimeError e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/delete")
    public void delete(@RequestParam("index") String index) {
        try {
            ImageService.delete(index);
        } catch (RuntimeError e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/update")
    public void update(@RequestParam("index") String index, @RequestParam("img") MultipartFile img) {
        try {
            ImageService.update(index, img);
        } catch (RuntimeError e) {
            throw new RuntimeException(e);
        }
    }
}