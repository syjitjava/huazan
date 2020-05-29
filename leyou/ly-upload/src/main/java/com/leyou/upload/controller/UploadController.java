package com.leyou.upload.controller;

import com.leyou.upload.service.UploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
public class UploadController {

    @Autowired
    private UploadService uploadService;

    @PostMapping("/image")
    public ResponseEntity<String> uploadImg(@RequestParam(name = "file") MultipartFile file){
        return ResponseEntity.ok(this.uploadService.upload(file));
    }
    @GetMapping("/signature")
    public ResponseEntity<Map<String ,Object>> signature(){
        return ResponseEntity.ok(uploadService.signature());
    }
}
