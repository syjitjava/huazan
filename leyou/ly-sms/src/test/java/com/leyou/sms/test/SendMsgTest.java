package com.leyou.sms.test;

import com.leyou.common.Constants.MQConstants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

import static com.leyou.common.Constants.MQConstants.Exchange.SMS_EXCHANGE_NAME;
import static com.leyou.common.Constants.MQConstants.RoutingKey.VERIFY_CODE_KEY;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SendMsgTest {

    @Autowired
    private AmqpTemplate amqp;

    @Test
    public void sendSms(){
        Map<String, String> map = new HashMap<>();
        map.put("phone","17603679708");
        map.put("code","234578");
        amqp.convertAndSend(SMS_EXCHANGE_NAME, VERIFY_CODE_KEY,map);

    }
}
