package com.leyou.search.client;

import com.leyou.LySearchApplication;
import com.leyou.common.vo.PageResult;
import com.leyou.item.client.ItemClient;
import com.leyou.item.dto.SpuDTO;
import com.leyou.scarch.pojo.Goods;
import com.leyou.scarch.repository.GoodsRepository;
import com.leyou.scarch.service.SearchService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = LySearchApplication.class)
public class LoadDateTest {

    @Autowired
    private SearchService searchService;

    @Autowired
    private ItemClient itemClient;

    @Autowired
    private GoodsRepository goodsRepository;

    @Test
    public void loadDate() {
        int page = 1;
        int size = 50;
        while (true){
            //        从item服务中获取spu的信息
            PageResult<SpuDTO> pageResult = itemClient.findSpuByPage(page, size, null, true);
            List<SpuDTO> spuDTOList = pageResult.getItems();
//            退出条件
            if(CollectionUtils.isEmpty(spuDTOList)){
                break;
            }
//        循环
            List<Goods> goodsList = new ArrayList<>();
            for (SpuDTO spuDTO : spuDTOList) {
                Goods goods = searchService.createGoods(spuDTO);
                System.out.println(goods);
                goodsList.add(goods);
            }
//            保存goods到es库  批量
            goodsRepository.saveAll(goodsList);
            if(spuDTOList.size()<50){
                break;
            }
            page++;
        }
    }
}