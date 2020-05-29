package com.leyou.user.dto;

import lombok.Data;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @author HM
 * @since 2020-03-31
 */
@Data
public class UserDTO {
    private Long id;

    /**
     * 用户名
     */
    private String username;


    /**
     * 注册手机号
     */
    private String phone;

}
