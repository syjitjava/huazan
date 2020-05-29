package com.leyou.item.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.leyou.item.dto.CategoryDTO;
import com.leyou.item.entity.TbCategory;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 商品类目表，类目和商品(spu)是一对多关系，类目与品牌是多对多关系 Mapper 接口
 * </p>
 *
 * @author HM
 * @since 2020-03-31
 */
public interface TbCategoryMapper extends BaseMapper<TbCategory> {

    @Select("SELECT b.id,b.name,b.parent_id,b.is_parent FROM tb_category_brand a ,tb_category b WHERE  a.`category_id` = b.id AND a.`brand_id` = #{brandId}")
    List<TbCategory> findCategoryByBrandId(@Param("brandId") Long id);
}
