package cn.itcase.userservice.conproller;

import cn.itcase.pojo.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    @GetMapping("/{id}")
    public User findById(@PathVariable(name = "id") Long userId){
        User user = new User();
        user.setId(userId);
        user.setUserName("张三");
        return user;
    }
}
