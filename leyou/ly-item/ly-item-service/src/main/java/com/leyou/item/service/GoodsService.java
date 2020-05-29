package com.leyou.item.service;

import com.leyou.common.vo.PageResult;
import com.leyou.item.dto.SpuDTO;

import java.util.Map;

public interface GoodsService {

    PageResult<SpuDTO> findSpuByPage(Integer page, Integer rows, String key, Boolean saleable);

    void updateGoods(SpuDTO spuDTO);

    void minusStock(Map<Long, Integer> cartMap);
}
