package com.leyou.page.listener;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 测试 消息消费者
 */
@Component
public class DemoListener {

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "demoqueue",durable = "true"),//队列
            exchange = @Exchange(value = "demoexchage",type = ExchangeTypes.TOPIC),//交换机
            key = {"test.#"}
    ))
    public void getMsg(String msg){
        System.out.println(msg);
    }
}
