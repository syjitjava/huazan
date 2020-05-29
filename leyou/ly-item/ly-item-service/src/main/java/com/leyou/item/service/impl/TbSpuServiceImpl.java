package com.leyou.item.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leyou.common.Constants.MQConstants;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.BeanHelper;
import com.leyou.common.vo.PageResult;
import com.leyou.item.dto.SkuDTO;
import com.leyou.item.dto.SpuDTO;
import com.leyou.item.dto.SpuDetailDTO;
import com.leyou.item.entity.TbSku;
import com.leyou.item.entity.TbSpu;
import com.leyou.item.entity.TbSpuDetail;
import com.leyou.item.mapper.TbSpuMapper;
import com.leyou.item.service.TbSkuService;
import com.leyou.item.service.TbSpuDetailService;
import com.leyou.item.service.TbSpuService;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

import static com.leyou.common.Constants.MQConstants.Exchange.ITEM_EXCHANGE_NAME;
import static com.leyou.common.Constants.MQConstants.RoutingKey.*;

/**
 * <p>
 * spu表，该表描述的是一个抽象性的商品，比如 iphone8 服务实现类
 * </p>
 *
 * @author HM
 * @since 2020-03-31
 */
@Service
public class TbSpuServiceImpl extends ServiceImpl<TbSpuMapper, TbSpu> implements TbSpuService {

    @Autowired
    private TbSpuDetailService detailService;

    @Autowired
    private TbSkuService skuService;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSaleable(Long id, Boolean saleable) {
        if (saleable) {
            boolean b = this.baseMapper.updateSaleable(id, saleable);
            if (!b) {
                throw new LyException(ExceptionEnum.UPDATE_OPERATION_FAIL);
            }
            amqpTemplate.convertAndSend(ITEM_EXCHANGE_NAME, ITEM_UP_KEY,id);
        } else {
            boolean b = this.baseMapper.updateSaleable(id, saleable);
            if (!b) {
                throw new LyException(ExceptionEnum.UPDATE_OPERATION_FAIL);
            }
            amqpTemplate.convertAndSend(ITEM_EXCHANGE_NAME,MQConstants.RoutingKey.ITEM_DOWN_KEY,id);
        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveGoods(SpuDTO spuDTO) {
        TbSpu tbSpu = BeanHelper.copyProperties(spuDTO, TbSpu.class);
        boolean bSpu = this.save(tbSpu);
        if (!bSpu) {
            throw new LyException(ExceptionEnum.INSERT_OPERATION_FAIL);
        }
        Long spuId = tbSpu.getId();
        //详情表
        SpuDetailDTO spuDetailDTO = spuDTO.getSpuDetail();
        TbSpuDetail tbSpuDetail = BeanHelper.copyProperties(spuDetailDTO, TbSpuDetail.class);
        tbSpuDetail.setSpuId(spuId);
        //保存spudetail信息
        boolean bDetail = detailService.save(tbSpuDetail);
        if (!bDetail) {
            throw new LyException(ExceptionEnum.INSERT_OPERATION_FAIL);
        }
        //保存sku信息
        List<SkuDTO> skus = spuDTO.getSkus();
        if (!CollectionUtils.isEmpty(skus)) {
            List<TbSku> tbSkuList = skus.stream().map(skuDTO -> {
                skuDTO.setSpuId(spuId);
                return BeanHelper.copyProperties(skuDTO, TbSku.class);
            }).collect(Collectors.toList());
            boolean bSku = skuService.saveBatch(tbSkuList);
            if (!bSku) {
                throw new LyException(ExceptionEnum.INSERT_OPERATION_FAIL);
            }
        }
    }

    @Override
    public SpuDTO findSpuById(Long spuId) {
        TbSpu tbSpu = this.getById(spuId);
        if (StringUtils.isEmpty(tbSpu)) {
            throw new LyException(ExceptionEnum.GOODS_NOT_FOUND);
        }
        SpuDTO spuDTO = BeanHelper.copyProperties(tbSpu, SpuDTO.class);
        return spuDTO;
    }
}