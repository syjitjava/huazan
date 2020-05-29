package com.leyou.page.controller;

import com.leyou.item.client.ItemClient;
import com.leyou.item.dto.BrandDTO;
import com.leyou.item.dto.SkuDTO;
import com.leyou.item.dto.SpuDTO;
import com.leyou.item.dto.SpuDetailDTO;
import com.leyou.page.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;

@Controller
public class PageController {

    @Autowired
    private PageService pageService;

    @GetMapping("/item/{id}.html")
    public String itemPage(@PathVariable(name = "id") Long spuId, Model model){
        Map<String, Object> Map = pageService.loadDate(spuId);
         model.addAllAttributes(Map);
        return "item";
    }
    @GetMapping("/item.html")
    public String a(){
        return "hello";
    }
}
