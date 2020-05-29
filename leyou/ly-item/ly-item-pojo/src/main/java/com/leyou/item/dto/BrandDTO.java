package com.leyou.item.dto;

import lombok.Data;

@Data
public class BrandDTO {

    private Long id;

    /**
     * 品牌名称
     */
    private String name;

    /**
     * 品牌图片地址
     */
    private String image;

    private String letter;
}
