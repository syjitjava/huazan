package com.leyou.item.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.BeanHelper;
import com.leyou.item.dto.CategoryDTO;
import com.leyou.item.entity.TbCategory;
import com.leyou.item.mapper.TbCategoryMapper;
import com.leyou.item.service.TbCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * <p>
 * 商品类目表，类目和商品(spu)是一对多关系，类目与品牌是多对多关系 服务实现类
 * </p>
 *
 * @author HM
 * @since 2020-03-31
 */
@Service
public class TbCategoryServiceImpl extends ServiceImpl<TbCategoryMapper, TbCategory> implements TbCategoryService {

    @Autowired
    private TbCategoryService categoryService;

    @Override
    public List<CategoryDTO> findByParentId(Long pid) {
        QueryWrapper<TbCategory> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(TbCategory::getParentId,pid);
        List<TbCategory> categoryList = this.list(queryWrapper);
        if (StringUtils.isEmpty(categoryList)) {
            throw new LyException(ExceptionEnum.CATEGORY_NOT_FOUND);
        }
        List<CategoryDTO> categoryDTOList = BeanHelper.copyWithCollection(categoryList, CategoryDTO.class);
        return categoryDTOList;
    }

    @Override
    public List<CategoryDTO> findByIds(List<Long> ids) {
        QueryWrapper<TbCategory> wrapper = new QueryWrapper<>();
        wrapper.lambda().in(TbCategory::getParentId,ids);
        List<TbCategory> categories = this.list(wrapper);
        List<CategoryDTO> categoryDTOS = BeanHelper.copyWithCollection(categories, CategoryDTO.class);
        if (StringUtils.isEmpty(categories)) {
            throw new LyException(ExceptionEnum.CATEGORY_NOT_FOUND);
        }
        return categoryDTOS;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveAndUpdateCategory(CategoryDTO categoryDTO) {
            TbCategory tbCategory = BeanHelper.copyProperties(categoryDTO, TbCategory.class);
        boolean b = categoryService.saveOrUpdate(tbCategory);
        if (!b) {
            throw new LyException(ExceptionEnum.UPDATE_OPERATION_FAIL);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCategoryById(Long id) {
        UpdateWrapper<TbCategory> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().eq(TbCategory::getId,id);
         this.remove(updateWrapper);
    }

    /**
     * 根据brandId查询品牌
     * @param id
     * @return
     */
    @Override
    public List<CategoryDTO> findCategoryByBrandId(Long id) {
        List<TbCategory> tbCategoryList = this.getBaseMapper().findCategoryByBrandId(id);
        if (CollectionUtils.isEmpty(tbCategoryList)) {
            throw new LyException(ExceptionEnum.CATEGORY_NOT_FOUND);
        }
       return BeanHelper.copyWithCollection(tbCategoryList,CategoryDTO.class);
    }
}
