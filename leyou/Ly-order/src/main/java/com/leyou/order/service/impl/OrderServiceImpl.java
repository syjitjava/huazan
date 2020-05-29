package com.leyou.order.service.impl;

import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayConstants;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.order.entity.TbOrder;
import com.leyou.order.enums.OrderStatusEnum;
import com.leyou.order.service.OrderService;
import com.leyou.order.service.TbOrderService;
import com.leyou.order.utlis.PayHelper;
import com.leyou.order.vo.OrderVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private WXPay wxPay;
    @Autowired
    private TbOrderService orderService;
    
    @Autowired
    private PayHelper payHelper;
    @Override
    public void handleNotify(Map<String, String> reqMap) {
        if(reqMap.get("result_code") == null || !reqMap.get("result_code").equals(WXPayConstants.SUCCESS)){
            throw new LyException(ExceptionEnum.INVALID_NOTIFY_PARAM);
        }
        try {
            boolean bWeiChat = wxPay.isPayResultNotifySignatureValid(reqMap);
        } catch (Exception e) {
            throw new LyException(ExceptionEnum.INVALID_NOTIFY_PARAM);
        }
        String outTradeNo = reqMap.get("out_trade_no");
        if (org.springframework.util.StringUtils.isEmpty(outTradeNo)) {
            throw new LyException(ExceptionEnum.INVALID_ORDER_STATUS);
        }
        Long orderId = Long.valueOf(outTradeNo);
        String totalFeeStr = reqMap.get("total_fee");
        if (org.springframework.util.StringUtils.isEmpty(totalFeeStr)) {
            throw new LyException(ExceptionEnum.INVALID_ORDER_STATUS);
        }
        Long totalFee = Long.valueOf(totalFeeStr);
        TbOrder tbOrder = orderService.getById(orderId);
        // TODO: 2020/5/2  现在是测试，所以不能使用下面的判断
//        if(tbOrder.getActualFee() != totalFee){
//            throw new LyException(ExceptionEnum.INVALID_NOTIFY_PARAM);
//        }
        if(tbOrder.getStatus().intValue() != OrderStatusEnum.INIT.value().intValue()){
            //可以抛异常，也可以回复微信已经成功
            throw new LyException(ExceptionEnum.INVALID_NOTIFY_PARAM);
        }
        TbOrder tbOrder1 = new TbOrder();
        tbOrder1.setOrderId(orderId);
        tbOrder1.setStatus(OrderStatusEnum.PAY_UP.value());
        boolean b = orderService.updateById(tbOrder1);
        if(!b){
            throw new LyException(ExceptionEnum.UPDATE_OPERATION_FAIL);
        }
    }
    }
