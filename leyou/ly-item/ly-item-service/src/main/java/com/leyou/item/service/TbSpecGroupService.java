package com.leyou.item.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.leyou.item.dto.SpecDTO;
import com.leyou.item.dto.SpecGroupDTO;
import com.leyou.item.dto.SpecParamDTO;
import com.leyou.item.entity.TbSpecGroup;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * <p>
 * 规格参数的分组表，每个商品分类下有多个规格参数组 服务类
 * </p>
 *
 * @author HM
 * @since 2020-03-31
 */
public interface TbSpecGroupService extends IService<TbSpecGroup> {

    List<SpecDTO> findGroupByCategoryId(Long categoryId);

    void saveSpecGroup(Long cid, String name);

    void updateSpecGroup(TbSpecGroup sg);

    void deleteSpecGroup(Long id);

    List<SpecGroupDTO> findSpecGroupByCategoryId(Long cid);
}
