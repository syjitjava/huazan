package com.leyou.item.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.leyou.item.entity.TbSku;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * <p>
 * sku表,该表表示具体的商品实体,如黑色的 64g的iphone 8 Mapper 接口
 * </p>
 *
 * @author HM
 * @since 2020-03-31
 */
public interface TbSkuMapper extends BaseMapper<TbSku> {
    @Update("UPDATE tb_sku SET stock = stock - #{num} WHERE id = #{id}")
    Integer minusStock(@Param("id") Long skuId, @Param("num") Integer num);
}
