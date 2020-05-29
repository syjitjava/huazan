package com.leyou.order.utlis;

import com.github.wxpay.sdk.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class PayHelper {

    @Autowired
    private WXPay wxPay;
    @Autowired
    private WXPayConfigImpl payConfig;

    public String createOrder(Long orderId,Long totalPay,String desc){
        Map<String,String> data = new HashMap<>();
        data.put("body",desc);
        data.put("out_trade_no",orderId.toString());
        data.put("total_fee",totalPay.toString());
        data.put("spbill_create_ip","127.0.0.1");
        data.put("notify_url",payConfig.getNotifyUrl());
        data.put("trade_type",payConfig.getPayType());
        Map<String, String> result = null;
        try {
           result = wxPay.unifiedOrder(data);
        } catch (Exception e) {
            log.error("【微信下单】创建预交易订单异常失败", e);
            throw new RuntimeException("微信下单失败",e);
        }
        checkResultCode(result);
        String codeUrl = result.get("code_url");
        if (StringUtils.isEmpty(codeUrl)) {
            log.error("【微信下单】创建预交易订单异常失败");
            throw new RuntimeException("微信下单失败返回链接异常");
        }
        return codeUrl;
    }
    public void checkResultCode(Map<String, String> result){
        String resultCode = result.get("result_code");
        if ("FAIL".equals(resultCode)) {
            log.error("【微信支付】微信支付业务失败，错误码：{}，原因：{}", result.get("err_code"), result.get("err_code_des"));
            throw new RuntimeException("【微信支付】微信支付业务失败");
        }
    }
    public void isValidSign(Map<String, String> result) throws Exception {
        boolean boo1 = WXPayUtil.isSignatureValid(result, payConfig.getKey(), WXPayConstants.SignType.MD5);
        boolean boo2 = WXPayUtil.isSignatureValid(result, payConfig.getKey(), WXPayConstants.SignType.HMACSHA256);
        if (!boo1 && !boo2) {
            throw new RuntimeException("【微信支付回调】签名有误");
        }
    }

}
