package com.leyou.cart.service;

import com.leyou.cart.entity.CartDTO;

import java.util.List;

public interface CartService {
    void addCart(CartDTO cartDTO);

    List<CartDTO> getCarts();

    void deleteCart(String skuId);

    void updateNum(Long skuId, Integer num);

    void addCarts(List<CartDTO> cartDTOS);
}
