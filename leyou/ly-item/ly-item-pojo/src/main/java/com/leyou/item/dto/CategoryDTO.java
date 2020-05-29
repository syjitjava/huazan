package com.leyou.item.dto;

import lombok.Data;


@Data
public class CategoryDTO {
    private Long id;

    /**
     * 类目名称
     */
    private String name;

    /**
     * 父类目id,顶级类目填0
     */
    private Long parentId;

    /**
     * 是否为父节点，0为否，1为是
     */
    private Boolean isParent;

    /**
     * 排序指数，越小越靠前
     */
    private Integer sort;



}

