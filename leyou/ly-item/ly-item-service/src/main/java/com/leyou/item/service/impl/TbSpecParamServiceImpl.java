package com.leyou.item.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.BeanHelper;
import com.leyou.item.dto.SpecGroupDTO;
import com.leyou.item.dto.SpecParamDTO;
import com.leyou.item.entity.TbSpecGroup;
import com.leyou.item.entity.TbSpecParam;
import com.leyou.item.mapper.TbSpecParamMapper;
import com.leyou.item.service.TbSpecParamService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * <p>
 * 规格参数组下的参数名 服务实现类
 * </p>
 *
 * @author HM
 * @since 2020-03-31
 */
@Service
public class TbSpecParamServiceImpl extends ServiceImpl<TbSpecParamMapper, TbSpecParam> implements TbSpecParamService {

    @Override
    public List<SpecParamDTO> findSpecParam(Long gid, Long cid,Boolean searching) {
        QueryWrapper<TbSpecParam> queryWrapper = new QueryWrapper<>();
        if(gid != null && gid != 0){
            queryWrapper.lambda().eq(TbSpecParam::getGroupId,gid);
        }
        if(cid != null && cid != 0){
            queryWrapper.lambda().eq(TbSpecParam::getCid,cid);
        }
        List<TbSpecParam> specParamList = this.list(queryWrapper);
        if (CollectionUtils.isEmpty(specParamList)) {
            throw new LyException(ExceptionEnum.SPEC_NOT_FOUND);
        }
        return BeanHelper.copyWithCollection(specParamList,SpecParamDTO.class);

    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveSpecParam(SpecParamDTO  specParamDTO) {
        if (StringUtils.isEmpty(specParamDTO)) {
            throw  new LyException(ExceptionEnum.INVALID_PARAM_ERROR);
        }
        TbSpecParam tbSpecParam = BeanHelper.copyProperties(specParamDTO, TbSpecParam.class);
        boolean b = this.save(tbSpecParam);
        if (!b) {
            throw new LyException(ExceptionEnum.INSERT_OPERATION_FAIL);
        }
    }


}
