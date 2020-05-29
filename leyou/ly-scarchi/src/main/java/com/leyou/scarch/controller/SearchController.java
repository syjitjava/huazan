package com.leyou.scarch.controller;

import com.leyou.common.vo.PageResult;
import com.leyou.scarch.dto.GoodsDTO;
import com.leyou.scarch.pojo.SearchRequest;
import com.leyou.scarch.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class SearchController {

    @Autowired
   private SearchService searchService;

    @PostMapping("/page")
    public ResponseEntity<PageResult<GoodsDTO>> search(@RequestBody SearchRequest searchRequest){
return ResponseEntity.ok(searchService.Search(searchRequest));
    }
    @PostMapping("filter")
    public ResponseEntity<Map<String,List<?>>> filter(@RequestBody SearchRequest request){
        return ResponseEntity.ok(searchService.filter(request));
    }
}
