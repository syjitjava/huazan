package com.leyou.scarch.service;

import com.leyou.common.vo.PageResult;
import com.leyou.item.dto.SpuDTO;
import com.leyou.scarch.dto.GoodsDTO;
import com.leyou.scarch.pojo.Goods;
import com.leyou.scarch.pojo.SearchRequest;

import java.util.List;
import java.util.Map;

public interface SearchService {
    Goods createGoods(SpuDTO spuDTO);

    PageResult<GoodsDTO> Search(SearchRequest request);

    Map<String,List<?>> filter(SearchRequest request);
}
