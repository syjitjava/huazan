package com.leyou.cart.controller;

import com.leyou.cart.entity.CartDTO;
import com.leyou.cart.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CartController {
    @Autowired
    private CartService cartService;

   @PostMapping
    public ResponseEntity<Void> addCart(@RequestBody CartDTO cartDTO){
       cartService.addCart(cartDTO);
       return ResponseEntity.noContent().build();
   }
   @GetMapping("/list")
    public ResponseEntity<List<CartDTO>> getCarts(){
      return ResponseEntity.ok(cartService.getCarts());
   }
   @DeleteMapping("{skuId}")
    public ResponseEntity deleteCart(@PathVariable String skuId){
       cartService.deleteCart(skuId);
       return ResponseEntity.noContent().build();
   }
   @PutMapping
    public ResponseEntity<Void> updateNum(@RequestParam(name = "id") Long skuId,
                                          @RequestParam(name = "num") Integer num){
       cartService.updateNum(skuId,num);
       return ResponseEntity.ok().build();
   }
    @PostMapping("/list")
    public ResponseEntity<Void> addCarts(@RequestBody List<CartDTO> cartDTOS){
        cartService.addCarts(cartDTOS);
        return ResponseEntity.ok().build();
    }
}
