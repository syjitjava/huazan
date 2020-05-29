package com.leyou.item.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.leyou.item.dto.CategoryDTO;
import com.leyou.item.entity.TbCategory;

import java.util.List;

/**
 * <p>
 * 商品类目表，类目和商品(spu)是一对多关系，类目与品牌是多对多关系 服务类
 * </p>
 *
 * @author HM
 * @since 2020-03-31
 */
public interface TbCategoryService extends IService<TbCategory> {

    List<CategoryDTO> findByParentId(Long pid);

    List<CategoryDTO> findByIds(List<Long> ids);

    void saveAndUpdateCategory(CategoryDTO categoryDTO);

    void deleteCategoryById(Long id);

    List<CategoryDTO> findCategoryByBrandId(Long id);
}
