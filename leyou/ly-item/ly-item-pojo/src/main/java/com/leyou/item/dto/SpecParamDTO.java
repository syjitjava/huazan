package com.leyou.item.dto;

import lombok.Data;
/**
 * <p>
 * 规格参数组下的参数名
 * </p>
 *
 * @author HM
 * @since 2020-03-31
 */
@Data

public class SpecParamDTO  {

private static final long serialVersionUID=1L;

    private Long id;

    /**
     * 商品分类id
     */
    private Long cid;

    private Long groupId;

    /**
     * 参数名
     */
    private String name;

    /**
     * 是否是数字类型参数，true或false
     */
    private Boolean IsNumeric;

    /**
     * 数字类型参数的单位，非数字类型可以为空
     */
    private String unit;

    /**
     * 是否是sku通用属性，true或false
     */
    private Boolean generic;

    /**
     * 是否用于搜索过滤，true或false
     */
    private Boolean searching;

    /**
     * 数值类型参数，如果需要搜索，则添加分段间隔值，如CPU频率间隔：0.5-1.0
     */
    private String segments;




}
