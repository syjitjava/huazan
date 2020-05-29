package com.leyou.item.controller;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.item.entity.TbCategory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/item")
public class ItemController {

    @PostMapping("/save")
    public ResponseEntity<TbCategory> saveCategory(TbCategory category){
if (StringUtils.isEmpty(category.getName())){
            throw new LyException(ExceptionEnum.NAME_NOT_NULL);
        }
         return ResponseEntity.ok(new TbCategory());
    }
}
