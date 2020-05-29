package com.leyou.scarch.listener;

import com.leyou.common.Constants.MQConstants;
import com.leyou.item.client.ItemClient;
import com.leyou.item.dto.SpuDTO;
import com.leyou.scarch.pojo.Goods;
import com.leyou.scarch.pojo.SearchRequest;
import com.leyou.scarch.repository.GoodsRepository;
import com.leyou.scarch.service.SearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.leyou.common.Constants.MQConstants.Queue.PAGE_ITEM_DOWN;
import static com.leyou.common.Constants.MQConstants.Queue.PAGE_ITEM_UP;
import static com.leyou.common.Constants.MQConstants.Queue.SEARCH_ITEM_UP;
import static com.leyou.common.Constants.MQConstants.RoutingKey.ITEM_DOWN_KEY;
import static com.leyou.common.Constants.MQConstants.RoutingKey.ITEM_UP_KEY;

@Slf4j
@Component
public class ItemListener {
    @Autowired
    private ItemClient itemClient;
    @Autowired
    private SearchService searchService;

    @Autowired
    private GoodsRepository repository;

    /**
     * 商品上架
     * @param spuId
     */
    @RabbitListener(bindings = @QueueBinding(value = @Queue(value = SEARCH_ITEM_UP
            ,durable = "true"),
            exchange = @Exchange(value = MQConstants.Exchange.ITEM_EXCHANGE_NAME,type = ExchangeTypes.TOPIC),key ={ITEM_UP_KEY

    }))
    public void itemUp(Long spuId){
        log.info("收到商品上架消息,spuId:{ }"+spuId);
        SpuDTO spuDTO = itemClient.findSpuById(spuId);
        Goods goods = searchService.createGoods(spuDTO);
        repository.save(goods);
    }

    /**
     * 商品下架
     * @param spuId
     */
    @RabbitListener(bindings = @QueueBinding(value = @Queue(value = PAGE_ITEM_DOWN,durable = "true"),
            exchange = @Exchange(value = MQConstants.Exchange.ITEM_EXCHANGE_NAME,type = ExchangeTypes.TOPIC),key ={ITEM_DOWN_KEY}))
    public void itemDown(Long spuId){
       repository.deleteById(spuId);
    }
}
