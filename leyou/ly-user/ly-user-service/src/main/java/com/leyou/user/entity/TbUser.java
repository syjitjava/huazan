package com.leyou.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.leyou.common.utils.constants.RegexPatterns;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @author HM
 * @since 2020-03-31
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class TbUser extends Model<TbUser> {

private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户名
     */
   @Pattern(regexp = RegexPatterns.USERNAME_REGEX,message = "用户名格式不正确")
    private String username;

    /**
     * 密码，加密存储
     */
    @Length(min = 6,max = 18,message = "密码格式不正确")
    private String password;

    /**
     * 注册手机号
     */
    @Pattern(regexp = RegexPatterns.PHONE_REGEX,message = "手机号格式不正确")
    private String phone;

    /**
     * 创建时间
     */

    private Date createTime;

    /**
     * 更新时间
     */

    private Date updateTime;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
