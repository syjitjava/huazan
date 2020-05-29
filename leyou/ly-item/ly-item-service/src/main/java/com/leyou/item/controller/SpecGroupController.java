package com.leyou.item.controller;

import com.leyou.item.dto.SpecDTO;
import com.leyou.item.dto.SpecParamDTO;
import com.leyou.item.entity.TbSpecGroup;
import com.leyou.item.service.TbSpecGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/spec")
public class SpecGroupController {

    @Autowired
    private TbSpecGroupService specGroupService;

    @GetMapping("/groups/of/category")
    public ResponseEntity<List<SpecDTO>> findGroupByCategoryId(@RequestParam(name = "id") Long categoryId){
           return ResponseEntity.ok( specGroupService.findGroupByCategoryId(categoryId));
    }
    @PostMapping("/group")
    public ResponseEntity<Void> saveSpecGroup(@RequestBody TbSpecGroup sg){
        specGroupService.saveSpecGroup(sg.getCid(),sg.getName());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping("/group")
    public ResponseEntity<Void> updateSpecGroup(@RequestBody TbSpecGroup sg){
        specGroupService.updateSpecGroup(sg);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
    @DeleteMapping("/group/{id}")
    public ResponseEntity<Void> deleteSpecGroup(@PathVariable("id") Long id){
        specGroupService.deleteSpecGroup(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
