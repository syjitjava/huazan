package com.leyou.user.controller;

import com.leyou.common.threadlocals.UserHolder;
import com.leyou.user.dto.AddressDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("address")
public class AddressController {

    @GetMapping
    public ResponseEntity<AddressDTO> queryAddressById(@RequestParam("id") Long id) {
        //从ThreadLocal中获取用户id
        Long userId = UserHolder.getTl();
        System.out.println("userId==" + userId);
        AddressDTO address = new AddressDTO();
        address.setUserId(userId);
        address.setId(1L);
        address.setStreet("顺义区马坡 传智播客");
        address.setCity("北京");
        address.setDistrict("顺义区");
        address.setAddressee("马坡");
        address.setPhone("15800000000");
        address.setProvince("北京");
        address.setPostcode("010000");
        address.setIsDefault(true);
        return ResponseEntity.ok(address);
    }
}