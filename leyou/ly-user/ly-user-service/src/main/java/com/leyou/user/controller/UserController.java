package com.leyou.user.controller;

import com.leyou.common.exception.LyException;
import com.leyou.user.dto.AddressDTO;
import com.leyou.user.dto.UserDTO;
import com.leyou.user.entity.TbUser;
import com.leyou.user.entity.User;
import com.leyou.user.service.TbUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.stream.Collectors;

@RestController
public class UserController {

    @Autowired
    private TbUserService userService;

    @GetMapping("/check/{data}/{type}")
    public ResponseEntity<Boolean> checkDate(@PathVariable(name = "data") String data,
                                             @PathVariable(name = "type") Integer type){
        return ResponseEntity.ok(userService.checkDate(data,type));
    }
    @PostMapping("/code")
    public ResponseEntity<Void> sendCode(@RequestParam("phone") String phone){
        userService.sendCode(phone);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid TbUser user, BindingResult result, @RequestParam("code")String code){
        if (result.hasErrors()) {
            String str = result.getFieldErrors().stream().map(FieldError::getDefaultMessage).collect(Collectors.joining(","));
            throw new LyException(400,str);
        }
        userService.register(user,code);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    @GetMapping("/query")
    public ResponseEntity<UserDTO> queryUser(@RequestParam(name = "username")String username,
                                         @RequestParam(name = "password")String password){
        return ResponseEntity.ok(userService.queryUser(username,password));
    }
}
