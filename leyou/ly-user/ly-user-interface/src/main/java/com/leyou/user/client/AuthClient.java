package com.leyou.user.client;

import com.leyou.user.dto.AddressDTO;
import com.leyou.user.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("user-service")
public interface AuthClient {

    @GetMapping("/query")
    UserDTO queryUser(@RequestParam(name = "username")String username,
                      @RequestParam(name = "password")String password);

    @GetMapping("/address")
    AddressDTO queryAddressById(@RequestParam("id") Long id);


}
