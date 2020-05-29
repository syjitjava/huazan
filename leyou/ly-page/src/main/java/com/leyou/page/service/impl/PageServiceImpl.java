package com.leyou.page.service.impl;

import com.leyou.item.client.ItemClient;
import com.leyou.item.dto.*;
import com.leyou.page.service.PageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class PageServiceImpl implements PageService {

    String  filePath ="D:\\javaAnZhuang\\nginx\\nginx-1.14.0\\html\\item";

    @Autowired
    private ItemClient itemClient;

    public Map<String,Object> loadDate(Long spuId){
        SpuDTO spuDTO = itemClient.findSpuById(spuId);
//        categories  分类集合
        List<CategoryDTO> categories = itemClient.findByIds(spuDTO.getCategoryIds());
//        brand 品牌
        Long brandId = spuDTO.getBrandId();
        BrandDTO brand = itemClient.findBrandById(brandId);
//        spuName 名字
        String spuName = spuDTO.getName();
//        subTitle 促销
        String subTitle = spuDTO.getSubTitle();
//        detail   spu详情
        SpuDetailDTO detail = itemClient.findSupDetailBySpuId(spuId);
//        skus sku的集合
        List<SkuDTO> skus = itemClient.findSkuBySpuId(spuId);
//        specs  规格参数、值  参数组的集合,以参数组为基本单位
        List<SpecGroupDTO> specs = itemClient.findSpecParamByCategoryId(spuDTO.getCid3());

        Map<String,Object> map = new HashMap<>();
        map.put("categories",categories);
        map.put("brand",brand);
        map.put("spuName",spuName);
        map.put("subTitle",subTitle);
        map.put("detail",detail);
        map.put("skus",skus);
        map.put("specs",specs);
        return map;
    }

    @Autowired
    private TemplateEngine templateEngine;

    @Transactional
    public void createItemHtml(Long id){
        Context context = new Context();
        context.setVariables(this.loadDate(id));

        File dir = new File(filePath);
        if (!dir.exists()) {
            dir.mkdir();
        }
        File file = new File(dir, id + ".html");
        PrintWriter writer = null;
        try {
           writer = new PrintWriter(file);
            templateEngine.process("item",context,writer);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("创建静态页面失败！spuId:{ }"+id);
        }finally {
            writer.close();
        }
    }

    @Override
    @Transactional
    public void deleteHtml(Long spuId) {
        File dir = new File(filePath);
        File file = new File(dir, spuId + ".html");
        file.delete();
    }
}
