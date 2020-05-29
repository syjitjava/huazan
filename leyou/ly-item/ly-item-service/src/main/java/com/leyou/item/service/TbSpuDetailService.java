package com.leyou.item.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.leyou.item.dto.SpuDetailDTO;
import com.leyou.item.entity.TbSpuDetail;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author HM
 * @since 2020-03-31
 */
public interface TbSpuDetailService extends IService<TbSpuDetail> {

    SpuDetailDTO findSupDetailBySpuId(Long spuId);
}
