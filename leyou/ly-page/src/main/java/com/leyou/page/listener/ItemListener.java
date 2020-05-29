package com.leyou.page.listener;

import com.leyou.common.Constants.MQConstants;
import com.leyou.page.service.PageService;
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
import static com.leyou.common.Constants.MQConstants.RoutingKey.ITEM_DOWN_KEY;
import static com.leyou.common.Constants.MQConstants.RoutingKey.ITEM_UP_KEY;
@Slf4j
@Component
public class ItemListener {
    @Autowired
    private PageService pageService;

    /**
     * 商品上架
     * @param spuId
     */
    @RabbitListener(bindings = @QueueBinding(value = @Queue(value = PAGE_ITEM_UP,durable = "true"),
            exchange = @Exchange(value = MQConstants.Exchange.ITEM_EXCHANGE_NAME,type = ExchangeTypes.TOPIC),key ={ITEM_UP_KEY}))
    public void itemUp(Long spuId){
log.info("收到商品上架消息,spuId:{ }"+spuId);
pageService.createItemHtml(spuId);
    }

    /**
     * 商品下架
     * @param spuId
     */
    @RabbitListener(bindings = @QueueBinding(value = @Queue(value = PAGE_ITEM_DOWN,durable = "true"),
            exchange = @Exchange(value = MQConstants.Exchange.ITEM_EXCHANGE_NAME,type = ExchangeTypes.TOPIC),key ={ITEM_DOWN_KEY}))
    public void itemDown(Long spuId){
        log.info("收到商品下架消息,spuId:{ }"+spuId);
        pageService.deleteHtml(spuId);
    }
}
