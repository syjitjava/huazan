package com.leyou.item.client;

import com.leyou.common.vo.PageResult;
import com.leyou.item.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 商品微服务的feign接口
 */
@FeignClient("item-service")
public interface ItemClient {

    @GetMapping("/sku/of/spu")
     List<SkuDTO> findSkuBySpuId(@RequestParam(name = "id") Long spuId);

    @GetMapping("/spec/params")
    List<SpecParamDTO> findSpecParam(@RequestParam(name = "gid",required = false) Long gid,
                                     @RequestParam(name = "cid", required = false) Long cid,
                                     @RequestParam(value = "searching", required = false) Boolean searching);

    @GetMapping("/spu/detail")
    SpuDetailDTO findSupDetailBySpuId(@RequestParam(name = "id") Long spuId);

    @GetMapping("/spu/page")
     PageResult<SpuDTO> findSpuByPage(
            @RequestParam(name = "page",defaultValue = "1") Integer page,
            @RequestParam(name = "rows",defaultValue = "10") Integer rows,
            @RequestParam(name = "key",required = false) String key,
            @RequestParam(name = "saleable",required = false) Boolean saleable);

    @GetMapping("/category/list")
   List<CategoryDTO> findByIds(@RequestParam(name = "ids") List<Long> ids);

    @GetMapping("/brand/list")
    List<BrandDTO> findBrandListByIds(@RequestParam(name = "ids") List<Long> ids);


    @GetMapping("/spu/{id}")
    SpuDTO findSpuById(@PathVariable(name = "id") Long spuId);

    @GetMapping("/brand/{id}")
    BrandDTO findBrandById(@PathVariable(name = "id") Long brandId);

    @GetMapping("/spec/of/category")
    List<SpecGroupDTO> findSpecParamByCategoryId(@RequestParam(name = "id") Long cid);

    @GetMapping("/sku/list")
    List<SkuDTO> findSkuBySkuIds(@RequestParam("ids")List<Long> skuIds);

    @PutMapping("/stock/minus")
    void minusStock(@RequestBody Map<Long, Integer> cartMap);
}
