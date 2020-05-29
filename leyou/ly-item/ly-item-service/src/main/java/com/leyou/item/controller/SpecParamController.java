package com.leyou.item.controller;

import com.leyou.item.dto.SpecGroupDTO;
import com.leyou.item.dto.SpecParamDTO;
import com.leyou.item.entity.TbSpecParam;
import com.leyou.item.service.TbSpecGroupService;
import com.leyou.item.service.TbSpecParamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/spec")
public class SpecParamController {
    @Autowired
    private TbSpecParamService specParamService;

    @Autowired
    private TbSpecGroupService SpecGroupService;

    @GetMapping("/params")
    public ResponseEntity<List<SpecParamDTO>> findSpecParam(@RequestParam(name = "gid",required = false) Long gid,
                                                            @RequestParam(name = "cid", required = false) Long cid,
                                                            @RequestParam(value = "searching", required = false) Boolean searching){
        List<SpecParamDTO> specParams = specParamService.findSpecParam(gid, cid,searching);
        return ResponseEntity.ok(specParams);
    }
    @PutMapping("/param")
    public ResponseEntity<Void> saveSpecParam(@RequestBody SpecParamDTO tbSpecParam){
       specParamService.saveSpecParam(tbSpecParam);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
    @GetMapping("/of/category")
    public ResponseEntity<List<SpecGroupDTO>> findSpecParamByCategoryId(@RequestParam(name = "id") Long cid){
      return  ResponseEntity.ok(SpecGroupService.findSpecGroupByCategoryId(cid));
    }
}
