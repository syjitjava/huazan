package com.leyou.item.controller;

import com.leyou.common.vo.PageResult;
import com.leyou.item.dto.SpuDTO;
import com.leyou.item.dto.SpuDetailDTO;
import com.leyou.item.service.GoodsService;
import com.leyou.item.service.TbSpuDetailService;
import com.leyou.item.service.TbSpuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
public class GoodsController {
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private TbSpuService tbSpuService;
    @Autowired
    private TbSpuDetailService tbSpuDetailService;

    @GetMapping("/spu/page")
    public ResponseEntity<PageResult> findSpuByPage(
            @RequestParam(name = "page",defaultValue = "1") Integer page,
            @RequestParam(name = "rows",defaultValue = "10") Integer rows,
            @RequestParam(name = "key",required = false) String key,
            @RequestParam(name = "saleable",required = false) Boolean saleable){
        PageResult<SpuDTO> pageResult = goodsService.findSpuByPage(page,rows,key,saleable);
        return ResponseEntity.ok(pageResult);
    }
    @PutMapping("/spu/saleable")
    public ResponseEntity<Void> updateSaleable(@RequestParam(name = "id") Long id,
                                               @RequestParam(name = "saleable") Boolean saleable){
        tbSpuService.updateSaleable(id,saleable);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
    @PostMapping("/goods")
    public ResponseEntity<Void> saveGoods(@RequestBody SpuDTO spuDTO){
        tbSpuService.saveGoods(spuDTO);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/spu/detail")
    public ResponseEntity<SpuDetailDTO> findSupDetailBySpuId(@RequestParam(name = "id") Long spuId){
        SpuDetailDTO spuDetailDTO = tbSpuDetailService.findSupDetailBySpuId(spuId);
        return ResponseEntity.ok(spuDetailDTO);
    }
    @PutMapping("/goods")
    public ResponseEntity<Void> updateGoods(@RequestBody SpuDTO spuDTO){
        goodsService.updateGoods(spuDTO);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/spu/{id}")
       public ResponseEntity<SpuDTO> findSpuById(@PathVariable(name = "id") Long spuId){
        return ResponseEntity.ok(tbSpuService.findSpuById(spuId));
    }
    @PutMapping("/stock/minus")
    public ResponseEntity<Void> minusStock(@RequestBody Map<Long, Integer> cartMap){
goodsService.minusStock(cartMap);
        return ResponseEntity.noContent().build();
    }
}
