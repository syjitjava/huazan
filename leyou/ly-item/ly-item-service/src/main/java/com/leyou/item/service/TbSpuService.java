package com.leyou.item.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.leyou.item.dto.SpuDTO;
import com.leyou.item.entity.TbSpu;
import com.leyou.common.vo.PageResult;
import org.springframework.http.ResponseEntity;

/**
 * <p>
 * spu表，该表描述的是一个抽象性的商品，比如 iphone8 服务类
 * </p>
 *
 * @author HM
 * @since 2020-03-31
 */
public interface TbSpuService extends IService<TbSpu> {

    void updateSaleable(Long id, Boolean saleable);

    void saveGoods(SpuDTO spuDTO);

    SpuDTO findSpuById(Long spuId);
}
