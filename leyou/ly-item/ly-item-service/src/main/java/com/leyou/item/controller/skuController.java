package com.leyou.item.controller;


import com.leyou.item.dto.SkuDTO;
import com.leyou.item.service.TbSkuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/sku")
public class skuController {

    @Autowired
    private TbSkuService tbSkuService;

    @GetMapping("/of/spu")
    public ResponseEntity<List<SkuDTO>>  findSkuBySpuId(@RequestParam(name = "id") Long spuId){
        List<SkuDTO> skuDTOS = tbSkuService.findSkuBySpuId(spuId);
        return ResponseEntity.ok(skuDTOS);
    }
    @GetMapping("/list")
    public ResponseEntity<List<SkuDTO>> findSkuByIds(@RequestParam(name = "ids") List<Long> ids){
return ResponseEntity.ok(tbSkuService.findSkuByIds(ids));
    }
    @GetMapping("/sku/list")
   public ResponseEntity<List<SkuDTO>> findSkuBySkuIds(@RequestParam("ids") List<Long> skuIds){
        return ResponseEntity.ok(tbSkuService.findSkuBySkuIds(skuIds));
    }
}
