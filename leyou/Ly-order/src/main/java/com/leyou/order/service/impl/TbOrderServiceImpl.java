package com.leyou.order.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.threadlocals.UserHolder;
import com.leyou.common.utils.BeanHelper;
import com.leyou.common.utils.IdWorker;
import com.leyou.item.client.ItemClient;
import com.leyou.item.dto.SkuDTO;
import com.leyou.order.dto.Cart;
import com.leyou.order.dto.OrderDTO;
import com.leyou.order.entity.TbOrder;
import com.leyou.order.entity.TbOrderDetail;
import com.leyou.order.entity.TbOrderLogistics;
import com.leyou.order.enums.OrderStatusEnum;
import com.leyou.order.mapper.TbOrderMapper;
import com.leyou.order.service.OrderService;
import com.leyou.order.service.TbOrderDetailService;
import com.leyou.order.service.TbOrderLogisticsService;
import com.leyou.order.service.TbOrderService;
import com.leyou.order.utlis.PayHelper;
import com.leyou.order.vo.OrderVO;
import com.leyou.user.client.AuthClient;
import com.leyou.user.dto.AddressDTO;
import com.sun.xml.internal.bind.v2.TODO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author HM
 * @since 2020-03-31
 */
@Service
public class TbOrderServiceImpl extends ServiceImpl<TbOrderMapper, TbOrder> implements TbOrderService {

    @Autowired
    private IdWorker idWorker;
    @Autowired
    private ItemClient itemClient;
    @Autowired
    private TbOrderDetailService orderDetailService;
    @Autowired
    private AuthClient userClient;
    @Autowired
    private TbOrderLogisticsService  orderLogisticsService;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private PayHelper payHelper;
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createOrder(OrderDTO orderDTO) {
        Long userId = UserHolder.getTl();
        Long orderId = idWorker.nextId();
        List<Cart> carts = orderDTO.getCarts();
        Map<Long, Integer> skuNumMap = carts.stream().collect(Collectors.toMap(Cart::getSkuId, Cart::getNum));
        List<Long> skuIds = carts.stream().map(Cart::getSkuId).collect(Collectors.toList());
        List<SkuDTO> skuDTOList = itemClient.findSkuBySkuIds(skuIds);
        Long totalFee = 0L;
        List<TbOrderDetail> tbOrderDetails = new ArrayList<>();

        for (SkuDTO skuDTO : skuDTOList) {
          Integer num = skuNumMap.get(skuDTO.getId());
            Long price = skuDTO.getPrice();
            totalFee += (price.longValue() * num.intValue());
            TbOrderDetail tbOrderDetail = new TbOrderDetail();
            tbOrderDetail.setImage(StringUtils.substringBefore(skuDTO.getImages(),","));
            tbOrderDetail.setOwnSpec(skuDTO.getOwnSpec());
            tbOrderDetail.setTitle(skuDTO.getTitle());
            tbOrderDetail.setSkuId(skuDTO.getId());
            tbOrderDetail.setOrderId(orderId);
            tbOrderDetail.setNum(num);
            tbOrderDetail.setPrice(price);
            tbOrderDetails.add(tbOrderDetail);
        }
        long  postFee = 0L;
        long youHuiFee =0L;
        Long actualFee = totalFee + postFee - youHuiFee;
        TbOrder tbOrder = new TbOrder();
        tbOrder.setOrderId(orderId);
        tbOrder.setUserId(userId);
        tbOrder.setStatus(OrderStatusEnum.INIT.value());
        tbOrder.setActualFee(actualFee);
        tbOrder.setPostFee(postFee);
        tbOrder.setTotalFee(totalFee);
        tbOrder.setPaymentType(orderDTO.getPaymentType());
        boolean b = this.save(tbOrder);
        if (!b) {
            throw new LyException(ExceptionEnum.INSERT_OPERATION_FAIL);
        }
        boolean b1 = orderDetailService.saveBatch(tbOrderDetails);
        if (!b1) {
            throw new LyException(ExceptionEnum.INSERT_OPERATION_FAIL);
        }
        AddressDTO addressDTO = userClient.queryAddressById(1L);
        if(addressDTO == null){
            log.error("获取 收货人信息失败！");
            throw new LyException(ExceptionEnum.INSERT_OPERATION_FAIL);
        }
        TbOrderLogistics tbOrderLogistics = BeanHelper.copyProperties(addressDTO,TbOrderLogistics.class);
        tbOrderLogistics.setOrderId(orderId);
        boolean b2 = orderLogisticsService.save(tbOrderLogistics);
        if(!b2){
            log.error("保存物流信息失败");
            throw new LyException(ExceptionEnum.INSERT_OPERATION_FAIL);
        }
//        减库存,远程调用item
        itemClient.minusStock(skuNumMap);


        return orderId;
    }
    public OrderVO findOrderById(Long orderId) {
        TbOrder tbOrder = this.getById(orderId);
        if(tbOrder == null){
            throw new LyException(ExceptionEnum.ORDER_NOT_FOUND);
        }

        return BeanHelper.copyProperties(tbOrder,OrderVO.class);
    }

    private String payUrlKey = "ly:pay:orderid:";
    @Override
    public String getUrl(Long orderId) {
        String payOrderUrl = payUrlKey + orderId;
        String url = redisTemplate.opsForValue().get(payOrderUrl);
        if (!org.springframework.util.StringUtils.isEmpty(url)) {
            return url;
        }
        TbOrder tbOrder = this.getById(orderId);
        if (tbOrder == null) {
            throw new LyException(ExceptionEnum.ORDER_DETAIL_NOT_FOUND);
        }
        if (tbOrder.getStatus().intValue() != OrderStatusEnum.INIT.value().intValue()) {
            throw new LyException(ExceptionEnum.INVALID_ORDER_STATUS);
        }
        // TODO: 2020/5/2  测试：totalFee=1分钱
       Long totalFee = 1L;
        String desc = "乐优商城商品支付";
        String payUrl = payHelper.createOrder(orderId, totalFee, desc);
        redisTemplate.opsForValue().set(payOrderUrl,payUrl,2, TimeUnit.HOURS);
        return payUrl;
    }

    public Integer findPayStatus(Long orderId) {
        TbOrder tbOrder = this.getById(orderId);
        if(tbOrder == null){
            throw new LyException(ExceptionEnum.ORDER_NOT_FOUND);
        }
        return tbOrder.getStatus();
    }
}