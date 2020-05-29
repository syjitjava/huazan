package com.leyou.item.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.leyou.item.entity.TbBrand;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 品牌表，一个品牌下有多个商品（spu），一对多关系 Mapper 接口
 * </p>
 *
 * @author HM
 * @since 2020-03-31
 */
public interface TbBrandMapper extends BaseMapper<TbBrand> {

    @Select("SELECT b.`id`,b.`name`,b.`image`,b.`letter` FROM `tb_category` c,`tb_brand` b,`tb_category_brand` cb WHERE c.`id` = cb.`category_id` AND b.`id` = cb.`brand_id` AND c.`id` = #{id}")
    List<TbBrand> findBrandByCategoryId(@Param("id") Long cid);
}
