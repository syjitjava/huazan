package com.leyou.item.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.BeanHelper;
import com.leyou.item.dto.SkuDTO;
import com.leyou.item.entity.TbSku;
import com.leyou.item.mapper.TbSkuMapper;
import com.leyou.item.service.TbSkuService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;

/**
 * <p>
 * sku表,该表表示具体的商品实体,如黑色的 64g的iphone 8 服务实现类
 * </p>
 *
 * @author HM
 * @since 2020-03-31
 */
@Service
public class TbSkuServiceImpl extends ServiceImpl<TbSkuMapper, TbSku> implements TbSkuService {

    @Override
    public List<SkuDTO> findSkuBySpuId(Long spuId) {
        QueryWrapper<TbSku> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(TbSku::getSpuId,spuId);
        List<TbSku> tbSkus = this.list(queryWrapper);
        if (CollectionUtils.isEmpty(tbSkus)) {
            throw new LyException(ExceptionEnum.GOODS_NOT_FOUND);
        }
       return BeanHelper.copyWithCollection(tbSkus, SkuDTO.class);
    }

    @Override
    public List<SkuDTO> findSkuByIds(List<Long> ids) {
        List<TbSku> tbSkus = (List<TbSku>) this.listByIds(ids);
        if (CollectionUtils.isEmpty(tbSkus)) {
            throw  new LyException(ExceptionEnum.GOODS_NOT_FOUND);
        }
        return BeanHelper.copyWithCollection(tbSkus,SkuDTO.class);
    }

    @Override
    public List<SkuDTO> findSkuBySkuIds(List<Long> skuIds) {
        List<TbSku> tbSkus = (List<TbSku>) this.listByIds(skuIds);
        if (CollectionUtils.isEmpty(tbSkus)) {
            throw  new LyException(ExceptionEnum.GOODS_NOT_FOUND);
        }
        return BeanHelper.copyWithCollection(tbSkus,SkuDTO.class);
    }
}
