package com.leyou.order.controller;

import com.leyou.order.dto.OrderDTO;
import com.leyou.order.service.TbOrderService;
import com.leyou.order.vo.OrderVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private TbOrderService orderService;
    @PostMapping
    public ResponseEntity<Long> createOrder(@RequestBody OrderDTO orderDTO){
        Long orderId = orderService.createOrder(orderDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(orderId);
    }
    @GetMapping("{id}")
    public ResponseEntity<OrderVO> findOrderById(@PathVariable("id") Long orderId){
        return ResponseEntity.ok(orderService.findOrderById(orderId));
    }
    @GetMapping("/url/{id}")
    public ResponseEntity<String> getPayUrl(@PathVariable("id") Long orderId) {
        return ResponseEntity.ok(orderService.getUrl(orderId));
    }
    @GetMapping("/state/{id}")
    public ResponseEntity<Integer> findPayState(@PathVariable("id") Long orderId) {
        return ResponseEntity.ok(orderService.findPayStatus(orderId));
    }
}
