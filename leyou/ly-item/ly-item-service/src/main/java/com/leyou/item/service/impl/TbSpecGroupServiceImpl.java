package com.leyou.item.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.BeanHelper;
import com.leyou.item.dto.SpecDTO;
import com.leyou.item.dto.SpecGroupDTO;
import com.leyou.item.dto.SpecParamDTO;
import com.leyou.item.entity.TbSpecGroup;
import com.leyou.item.entity.TbSpecParam;
import com.leyou.item.mapper.TbSpecGroupMapper;
import com.leyou.item.service.TbSpecGroupService;
import com.leyou.item.service.TbSpecParamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 规格参数的分组表，每个商品分类下有多个规格参数组 服务实现类
 * </p>
 *
 * @author HM
 * @since 2020-03-31
 */
@Service
public class TbSpecGroupServiceImpl extends ServiceImpl<TbSpecGroupMapper, TbSpecGroup> implements TbSpecGroupService {

    @Autowired
    private TbSpecParamService tbSpecParamService;

    @Override
    public List<SpecDTO> findGroupByCategoryId(Long categoryId) {
        QueryWrapper<TbSpecGroup> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(TbSpecGroup::getCid,categoryId);
        List<TbSpecGroup> SpecGroups = this.list(queryWrapper);
        if (CollectionUtils.isEmpty(SpecGroups)) {
            throw  new LyException(ExceptionEnum.SPEC_NOT_FOUND);
        }
        return BeanHelper.copyWithCollection(SpecGroups, SpecDTO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveSpecGroup(Long cid, String name) {
        TbSpecGroup tbSpecGroup = new TbSpecGroup();
        tbSpecGroup.setCid(cid);
        tbSpecGroup.setName(name);
        boolean b = this.save(tbSpecGroup);
        if (!b) {
            throw new LyException(ExceptionEnum.INSERT_OPERATION_FAIL);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSpecGroup(TbSpecGroup sg) {
        boolean b = this.updateById(sg);
        if (!b) {
            throw new LyException(ExceptionEnum.UPDATE_OPERATION_FAIL);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteSpecGroup(Long id) {
        boolean b = this.removeById(id);
        if (!b) {
            throw new LyException(ExceptionEnum.DELETE_OPERATION_FAIL);
        }
    }

    @Override
    public List<SpecGroupDTO> findSpecGroupByCategoryId(Long cid) {
        QueryWrapper<TbSpecGroup> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(TbSpecGroup::getCid,cid);
        List<TbSpecGroup> specGroupList = this.list(queryWrapper);
        if (CollectionUtils.isEmpty(specGroupList)) {
            throw  new LyException(ExceptionEnum.CATEGORY_NOT_FOUND);
        }

        List<SpecGroupDTO> specGroupDTOS = BeanHelper.copyWithCollection(specGroupList, SpecGroupDTO.class);
        List<SpecParamDTO> specParamDTOList = tbSpecParamService.findSpecParam(null, cid, null);
        ArrayList<SpecParamDTO> list = new ArrayList<>();
        for (SpecGroupDTO groupDTO : specGroupDTOS) {
            for (SpecParamDTO specParamDTO : specParamDTOList) {
                if (groupDTO.getId().longValue() == specParamDTO.getGroupId().longValue()) {
                    list.add(specParamDTO);
                }
            }
           groupDTO.setParams(list);
        }
return specGroupDTOS;
    }
}
