package com.leyou.item.dto;

import lombok.Data;

/**
 * <p>
 * 规格参数的分组表，每个商品分类下有多个规格参数组
 * </p>
 *
 * @author HM
 * @since 2020-03-31
 */
@Data
public class SpecDTO {

private static final long serialVersionUID=1L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 商品分类id，一个分类下有多个规格组
     */
    private Long cid;

    /**
     * 规格组的名称
     */
    private String name;


}
