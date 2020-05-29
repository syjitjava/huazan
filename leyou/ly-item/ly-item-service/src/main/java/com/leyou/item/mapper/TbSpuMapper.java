package com.leyou.item.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.leyou.item.entity.TbSpu;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * <p>
 * spu表，该表描述的是一个抽象性的商品，比如 iphone8 Mapper 接口
 * </p>
 *
 * @author HM
 * @since 2020-03-31
 */
public interface TbSpuMapper extends BaseMapper<TbSpu> {
@Update("UPDATE `tb_spu` SET saleable = #{saleable} WHERE id = #{id}")
    boolean updateSaleable(@Param("id") Long id, @Param("saleable") Boolean saleable);
}
