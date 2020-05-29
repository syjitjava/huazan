package com.leyou.item.controller;

import com.leyou.common.utils.BeanHelper;
import com.leyou.item.dto.BrandDTO;
import com.leyou.item.dto.CategoryDTO;
import com.leyou.item.entity.TbCategory;
import com.leyou.item.service.TbCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private TbCategoryService categoryService;

    @GetMapping("/of/parent")
    public ResponseEntity<List<CategoryDTO>> findByParentId(@RequestParam(name = "pid") Long pid){
        List<CategoryDTO> categoryDTOList = categoryService.findByParentId(pid);
        return ResponseEntity.ok(categoryDTOList);
    }
    @GetMapping("/list")
    public ResponseEntity<List<CategoryDTO>> findByIds(@RequestParam(name = "ids") List<Long> ids){
        List<CategoryDTO> categoryList = categoryService.findByIds(ids);
        return ResponseEntity.ok(categoryList);
    }

    @GetMapping("/of/brand")
    public ResponseEntity<List<CategoryDTO>> findCategoryByBrandId(@RequestParam(name = "id") Long id){
        List<CategoryDTO> categoryDTOS = categoryService.findCategoryByBrandId(id);
        return ResponseEntity.ok(categoryDTOS);
    }
    @PostMapping
    public ResponseEntity<Void> saveCategory(@RequestBody CategoryDTO categoryDTO){
        categoryService.saveAndUpdateCategory(categoryDTO);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
    @PutMapping
    public ResponseEntity<Void> updateCategory(@RequestBody CategoryDTO categoryDTO){
        categoryService.saveAndUpdateCategory(categoryDTO);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
    @DeleteMapping
    public ResponseEntity<Void> deleteCategoryById(@RequestParam(name = "id") Long id ){
        System.err.println(id);
        categoryService.deleteCategoryById(id);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
