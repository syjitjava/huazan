package com.leyou.cart.service.impl;

import com.leyou.cart.entity.CartDTO;
import com.leyou.cart.service.CartService;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.threadlocals.UserHolder;
import com.leyou.common.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CartServiceImpl implements CartService {

    @Autowired
    private StringRedisTemplate template;

    private static final String KEY_PREFIX = "ly:cart:uid";

    @Override
    public void addCart(CartDTO cartDTO) {
        String key = KEY_PREFIX + UserHolder.getTl().toString();
        BoundHashOperations<String, String , String> ops = template.boundHashOps(key);
        String hashKey = cartDTO.getSkuId().toString();
        int num = cartDTO.getNum();
        Boolean boo = ops.hasKey(hashKey);
        if(boo != null && boo){
             cartDTO = JsonUtils.toBean(ops.get(hashKey), CartDTO.class);
            cartDTO.setNum(cartDTO.getNum()+num);
        }
        ops.put(hashKey,JsonUtils.toString(cartDTO));
    }

    @Override
    public List<CartDTO> getCarts() {
        String key = KEY_PREFIX + UserHolder.getTl().toString();
        Boolean boo = template.hasKey(key);
        if(boo == null && !boo){
            throw new LyException(ExceptionEnum.CARTS_NOT_FOUND);
        }
        BoundHashOperations<String, String , String> ops = template.boundHashOps(key);
        Long size = ops.size();
        if(size == null || size < 0){
            // 不存在，直接返回
            throw new LyException(ExceptionEnum.CARTS_NOT_FOUND);
        }
        List<String> carts = ops.values();
       return carts.stream().map(json -> JsonUtils.toBean(json, CartDTO.class)).collect(Collectors.toList());
    }

    @Override
    public void deleteCart(String skuId) {
        String key = KEY_PREFIX + UserHolder.getTl().toString();
        template.opsForHash().delete(key,skuId);
    }

    @Override
    public void updateNum(Long skuId, Integer num) {
        String key = KEY_PREFIX + UserHolder.getTl().toString();
        BoundHashOperations<String, String , String> ops = template.boundHashOps(key);
        String hashKey = skuId.toString();
        Boolean boo = template.hasKey(key);
        if(boo == null && !boo){
            log.error("购物车商品不存在，用户：{}, 商品：{}", skuId);
            throw new LyException(ExceptionEnum.CARTS_NOT_FOUND);
        }
        CartDTO cartDTO = JsonUtils.toBean(ops.get(hashKey), CartDTO.class);
        cartDTO.setNum(num);
        ops.put(hashKey,JsonUtils.toString(cartDTO));
    }

    @Override
    public void addCarts(List<CartDTO> cartDTOS) {
        String key = KEY_PREFIX + UserHolder.getTl().toString();
        BoundHashOperations<String, String , String> ops = template.boundHashOps(key);
        for (CartDTO cartDTO : cartDTOS) {
            Integer num = cartDTO.getNum();
            String hashKey = cartDTO.getSkuId().toString();
            String jsonKey = ops.get(hashKey);
            if (!StringUtils.isEmpty(jsonKey)) {
                 cartDTO = JsonUtils.toBean(jsonKey, CartDTO.class);
                 cartDTO.setNum(cartDTO.getNum()+num);
            }
            ops.put(hashKey,JsonUtils.toString(cartDTO));
        }
    }
}
