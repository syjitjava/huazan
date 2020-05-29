package com.leyou.item.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.BeanHelper;
import com.leyou.common.vo.PageResult;
import com.leyou.item.dto.SkuDTO;
import com.leyou.item.dto.SpuDTO;
import com.leyou.item.dto.SpuDetailDTO;
import com.leyou.item.entity.*;
import com.leyou.item.mapper.TbSkuMapper;
import com.leyou.item.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GoodsServiceImpl implements GoodsService {


    @Autowired
    private TbSpuService spuService;

    /**
     * 分页查询spu信息
     *
     * @param page
     * @param rows
     * @param key
     * @param saleable
     * @return
     */
    public PageResult<SpuDTO> findSpuByPage(Integer page, Integer rows, String key, Boolean saleable) {
//        构造分页条件
        IPage<TbSpu> page1 = new Page<>(page, rows);
//        构造查询条件
        QueryWrapper<TbSpu> queryWrapper = new QueryWrapper<>();
        if (!StringUtils.isEmpty(key)) {
            queryWrapper.lambda().like(TbSpu::getName, key);
        }
        if (saleable != null) {
            queryWrapper.lambda().eq(TbSpu::getSaleable, saleable);
        }
        queryWrapper.lambda().orderByDesc(TbSpu::getUpdateTime);
//        分页查询
        IPage<TbSpu> spuIPage = spuService.page(page1, queryWrapper);
        List<TbSpu> tbSpuList = spuIPage.getRecords();
        if (CollectionUtils.isEmpty(tbSpuList)) {
            throw new LyException(ExceptionEnum.GOODS_NOT_FOUND);
        }

        List<SpuDTO> spuDTOList = BeanHelper.copyWithCollection(tbSpuList, SpuDTO.class);
        handleGetCategoryNameAndBrandName(spuDTOList);
        return new PageResult<>(spuDTOList, spuIPage.getTotal(), spuIPage.getPages());
    }

    @Autowired
    private TbBrandService brandService;
    @Autowired
    private TbCategoryService categoryService;

    private void handleGetCategoryNameAndBrandName(List<SpuDTO> spuDTOList) {
        for (SpuDTO spuDTO : spuDTOList) { //商品所属 分类
            List<Long> categoryIdList = spuDTO.getCategoryIds();
            Collection<TbCategory> tbCategoryCollection = categoryService.listByIds(categoryIdList);
            String categoryName = tbCategoryCollection.stream().map(TbCategory::getName).collect(Collectors.joining("/"));
            spuDTO.setCategoryName(categoryName); //商品品牌
            Long brandId = spuDTO.getBrandId();
            TbBrand brand = brandService.getById(brandId);
            spuDTO.setBrandName(brand.getName());
        }
    }

    @Autowired
    private TbSpuDetailService tbSpuDetailService;
    @Autowired
    private TbSkuService tbSkuService;
    @Autowired
    private TbSkuMapper tbSkuMapper;

    @Transactional(rollbackFor = Exception.class)
    public void updateGoods(SpuDTO spuDTO) {
        TbSpu tbSpu = BeanHelper.copyProperties(spuDTO, TbSpu.class);
        boolean b = spuService.updateById(tbSpu);
        if (!b) {
            throw new LyException(ExceptionEnum.UPDATE_OPERATION_FAIL);
        }
        Long spuId = tbSpu.getId();
        SpuDetailDTO spuDetailDTO = spuDTO.getSpuDetail();
        TbSpuDetail tbSpuDetail = BeanHelper.copyProperties(spuDetailDTO, TbSpuDetail.class);
        boolean b1 = tbSpuDetailService.updateById(tbSpuDetail);
        if (!b1) {
            throw  new LyException(ExceptionEnum.UPDATE_OPERATION_FAIL);
        }
        QueryWrapper<TbSku> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(TbSku::getSpuId,spuId);
        boolean remove = tbSkuService.remove(queryWrapper);
        if (!remove) {
            throw  new LyException(ExceptionEnum.UPDATE_OPERATION_FAIL);
        }
        List<SkuDTO> skus = spuDTO.getSkus();
        List<TbSku> tbSkus = BeanHelper.copyWithCollection(skus, TbSku.class);
        for (TbSku tbSku : tbSkus) {
            tbSku.setSpuId(spuId);
        }
        boolean b2 = tbSkuService.saveBatch(tbSkus);
        if (!b2) {
            throw  new LyException(ExceptionEnum.UPDATE_OPERATION_FAIL);
        }
    }

    @Override
    public void minusStock(Map<Long, Integer> cartMap) {
        for (Long skuId : cartMap.keySet()) {
            Integer num = cartMap.get(skuId);
            int count = tbSkuMapper.minusStock(skuId,num);
            if(count != 1){
                throw new LyException(ExceptionEnum.INVALID_ORDER_STATUS);
            }
        }
    }
}