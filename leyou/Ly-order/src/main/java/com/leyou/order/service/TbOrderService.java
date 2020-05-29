package com.leyou.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.leyou.order.dto.OrderDTO;
import com.leyou.order.entity.TbOrder;
import com.leyou.order.vo.OrderVO;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author HM
 * @since 2020-03-31
 */
public interface TbOrderService extends IService<TbOrder> {

    Long createOrder(OrderDTO orderDTO);

    OrderVO findOrderById(Long orderId);

    String getUrl(Long orderId);

    Integer findPayStatus(Long orderId);
}
