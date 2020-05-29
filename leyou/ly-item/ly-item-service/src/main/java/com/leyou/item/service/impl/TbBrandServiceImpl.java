package com.leyou.item.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.BeanHelper;
import com.leyou.common.vo.PageResult;
import com.leyou.item.dto.BrandDTO;
import com.leyou.item.entity.TbBrand;
import com.leyou.item.entity.TbCategoryBrand;
import com.leyou.item.mapper.TbBrandMapper;
import com.leyou.item.service.TbBrandService;
import com.leyou.item.service.TbCategoryBrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * <p>
 * 品牌表，一个品牌下有多个商品（spu），一对多关系 服务实现类
 * </p>
 *
 * @author HM
 * @since 2020-03-31
 */
@Service
public class TbBrandServiceImpl extends ServiceImpl<TbBrandMapper, TbBrand> implements TbBrandService {


    @Autowired
    private TbCategoryBrandService categoryBrandService;

    //private  List tbList = new ArrayList<TbCategoryBrand>();

    @Override
    public PageResult<BrandDTO> findBrandByPage(String key, Integer page, Integer rows, String sortBy, Boolean desc) {
        IPage<TbBrand> Page1 = new Page<>(page, rows);
        QueryWrapper<TbBrand> wrapper = new QueryWrapper<>();
        if (!StringUtils.isEmpty(key)) {
            wrapper.lambda().like(TbBrand::getName,key).or().like(TbBrand::getLetter,key);
        }
        if (!StringUtils.isEmpty(sortBy)) {
            if (desc) {
                wrapper.orderByDesc(sortBy);
            }else {
                wrapper.orderByAsc(sortBy);
            }
        }
        IPage<TbBrand> brandIPage = this.page(Page1, wrapper);
        List<TbBrand> records = brandIPage.getRecords();
        if (CollectionUtils.isEmpty(records)) {
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        List<BrandDTO> brandDTOList = BeanHelper.copyWithCollection(records, BrandDTO.class);

        return new PageResult<BrandDTO>(brandDTOList,brandIPage.getTotal(), brandIPage.getPages());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBrand(TbBrand tbBrand, List<Long> cids) {
        boolean brandB = this.updateById(tbBrand);
        if (!brandB) {
            throw new LyException(ExceptionEnum.UPDATE_OPERATION_FAIL);
        }else {
        Long brandId = tbBrand.getId();
        if (!CollectionUtils.isEmpty(cids)) {
            //先删除
            QueryWrapper<TbCategoryBrand> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(TbCategoryBrand::getBrandId, brandId);
            boolean removeB = categoryBrandService.remove(queryWrapper);
            if (!removeB) {
                throw new LyException(ExceptionEnum.DELETE_OPERATION_FAIL);
            }
                //新增
                List list = new ArrayList<TbCategoryBrand>();
                for (Long cid : cids) {
                    TbCategoryBrand tbCategoryBrand = new TbCategoryBrand();
                    tbCategoryBrand.setBrandId(brandId);
                    tbCategoryBrand.setCategoryId(cid);
                    list.add(tbCategoryBrand);
                }
                categoryBrandService.saveBatch(list);
                if (!removeB) {
                    throw new LyException(ExceptionEnum.DELETE_OPERATION_FAIL);
                }
            }
        }
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveBrand(TbBrand brand, List<Long> cids) {
        boolean brandB = this.save(brand);
        if(!brandB){
            throw  new LyException(ExceptionEnum.INSERT_OPERATION_FAIL);
        }else {
            Long brandId = brand.getId();
            if (!CollectionUtils.isEmpty(cids)) {
                List list = new ArrayList<TbCategoryBrand>();
                for (Long cid : cids) {
                    TbCategoryBrand tbCategoryBrand = new TbCategoryBrand();
                    tbCategoryBrand.setBrandId(brandId);
                    tbCategoryBrand.setCategoryId(cid);
                    list.add(tbCategoryBrand);
                }
                //批量保存 分类品牌 中间表
                boolean b = categoryBrandService.saveBatch(list);
                if (!b) {
                    throw new LyException(ExceptionEnum.INSERT_OPERATION_FAIL);
                }
            }
        }
    }
    /**
     * 根据ID查询
     * @param id
     * @return
     */
    @Override
    public BrandDTO findBrandById(Long id) {
        TbBrand tbBrand = this.getById(id);
       return BeanHelper.copyProperties(tbBrand, BrandDTO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBrandById(Long id) {
        QueryWrapper<TbCategoryBrand> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(TbCategoryBrand::getBrandId,id);
        boolean b = this.removeById(id);
        if (!b) {
            boolean b1 = categoryBrandService.remove(queryWrapper);
            if (!b1) {
                throw new LyException(ExceptionEnum.DELETE_OPERATION_FAIL);
            }
        }

    }

    @Override
    public List<BrandDTO> findBrandByCategoryId(Long cid) {
        List<TbBrand> tbBrands = this.baseMapper.findBrandByCategoryId(cid);
        if (CollectionUtils.isEmpty(tbBrands)) {
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        return BeanHelper.copyWithCollection(tbBrands, BrandDTO.class);

    }

    @Override
    public List<BrandDTO> findBrandListByIds(List<Long> ids) {
       List<TbBrand> tbBrands = (List<TbBrand>) this.listByIds(ids);
        if (CollectionUtils.isEmpty(tbBrands)) {
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }
      return BeanHelper.copyWithCollection(tbBrands ,BrandDTO.class);
    }

}
