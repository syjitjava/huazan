package com.leyou.item.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.leyou.item.dto.SkuDTO;
import com.leyou.item.entity.TbSku;

import java.util.List;

/**
 * <p>
 * sku表,该表表示具体的商品实体,如黑色的 64g的iphone 8 服务类
 * </p>
 *
 * @author HM
 * @since 2020-03-31
 */
public interface TbSkuService extends IService<TbSku> {

    List<SkuDTO> findSkuBySpuId(Long spuId);

    List<SkuDTO> findSkuByIds(List<Long> ids);

    List<SkuDTO> findSkuBySkuIds(List<Long> skuIds);
}
