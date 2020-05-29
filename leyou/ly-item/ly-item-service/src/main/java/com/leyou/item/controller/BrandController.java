package com.leyou.item.controller;

import com.leyou.common.vo.PageResult;
import com.leyou.item.dto.BrandDTO;
import com.leyou.item.entity.TbBrand;
import com.leyou.item.service.TbBrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/brand")
public class BrandController {

    @Autowired
    private TbBrandService brandService;

    @GetMapping("/page")
    public ResponseEntity<PageResult<BrandDTO>> findBrandByPage(@RequestParam(name = "key",required = false)String key,
                                                                @RequestParam(name = "page",defaultValue = "1") Integer page,
                                                                @RequestParam(name = "rows",defaultValue = "10") Integer rows,
                                                                @RequestParam(name = "sortBy",required = false) String sortBy,
                                                                @RequestParam(name = "desc",defaultValue = "false") Boolean desc){
        return ResponseEntity.ok(brandService.findBrandByPage(key, page, rows, sortBy, desc));
    }
    @PostMapping
    public ResponseEntity<Void> saveBrand(TbBrand tbBrand,@RequestParam(name = "cids") List<Long> cids){
        brandService.saveBrand(tbBrand,cids);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
    @PutMapping
    public ResponseEntity<Void> updateBrand(TbBrand tbBrand,@RequestParam(name = "cids") List<Long> cids){
        brandService.updateBrand(tbBrand,cids);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 根据ID查询
     * @param id
     * @return brand实体
     */
    @GetMapping("/{id}")
    public ResponseEntity<BrandDTO> findBrandById(@PathVariable(name = "id") Long id){
        BrandDTO brandDTO = brandService.findBrandById(id);
        return ResponseEntity.ok(brandDTO);
    }
    @DeleteMapping
    public ResponseEntity<Void> deleteBrandById(@RequestParam(name = "id") Long id){
    brandService.deleteBrandById(id);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    @GetMapping("/of/category")
    public ResponseEntity<List<BrandDTO>> findBrandByCategoryId(@RequestParam(name = "id") Long cid){
        return ResponseEntity.ok( brandService.findBrandByCategoryId(cid));
    }
    @GetMapping("/list")
    public ResponseEntity<List<BrandDTO>> findBrandListByIds(@RequestParam(name = "ids") List<Long> ids){
        return ResponseEntity.ok( brandService.findBrandListByIds(ids));
    }

}
