package com.leyou.item.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.BeanHelper;
import com.leyou.item.dto.SpuDetailDTO;
import com.leyou.item.entity.TbSpuDetail;
import com.leyou.item.mapper.TbSpuDetailMapper;
import com.leyou.item.service.TbSpuDetailService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author HM
 * @since 2020-03-31
 */
@Service
public class TbSpuDetailServiceImpl extends ServiceImpl<TbSpuDetailMapper, TbSpuDetail> implements TbSpuDetailService {

    @Override
    public SpuDetailDTO findSupDetailBySpuId(Long spuId) {
        TbSpuDetail spuDetail = this.getById(spuId);
        SpuDetailDTO spuDetailDTO = BeanHelper.copyProperties(spuDetail, SpuDetailDTO.class);
        if (StringUtils.isEmpty(spuDetailDTO)) {
            throw new LyException(ExceptionEnum.GOODS_NOT_FOUND);
        }
        return spuDetailDTO;
    }
}
