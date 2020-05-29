package com.leyou.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.BeanHelper;
import com.leyou.common.utils.RegexUtils;
import com.leyou.user.dto.UserDTO;
import com.leyou.user.entity.TbUser;
import com.leyou.user.mapper.TbUserMapper;
import com.leyou.user.service.TbUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.leyou.common.Constants.MQConstants.Exchange.SMS_EXCHANGE_NAME;
import static com.leyou.common.Constants.MQConstants.RoutingKey.VERIFY_CODE_KEY;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author HM
 * @since 2020-03-31
 */
@Slf4j
@Service
public class TbUserServiceImpl extends ServiceImpl<TbUserMapper, TbUser> implements TbUserService {
    /**
     * 判断用户名密码是否重复
     * @param data ：返回数据
     * @param type  1:用户名 2:手机号
     * @return
     */
    @Override
    public Boolean checkDate(String data, Integer type) {
        QueryWrapper<TbUser> queryWrapper = new QueryWrapper<>();
        switch (type){
            case 1:
                queryWrapper.lambda().eq(TbUser::getUsername,data);
                break;
            case 2:
                queryWrapper.lambda().eq(TbUser::getPhone,data);
                break;
                default:
                    throw new LyException(ExceptionEnum.INVALID_PARAM_ERROR);
        }
        int count = this.count(queryWrapper);
        return count == 0;
    }

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private AmqpTemplate amqpTemplate;

    private static final String  KEY_PREFIX = "ly:user:verify:phone:";

    @Override
    public void sendCode(String phone) {
        // 验证手机号格式
        if (!RegexUtils.isPhone(phone)) {
            throw new LyException(ExceptionEnum.INVALID_PHONE_NUMBER);
        }
        String code = String.valueOf((int)((Math.random() * 9 + 1) * 100000));
        String key = KEY_PREFIX + phone;
        redisTemplate.opsForValue().set(key,code,5, TimeUnit.MINUTES);
        Map<String, String> map = new HashMap<>();
        map.put("phone",phone);
        map.put("code",code);
       amqpTemplate.convertAndSend(SMS_EXCHANGE_NAME,VERIFY_CODE_KEY,map);
    }

    @Autowired
    private BCryptPasswordEncoder encoder;
    @Override
    public void register(TbUser user, String code) {
        String key = KEY_PREFIX + user.getPhone();
        String cacheCode = redisTemplate.opsForValue().get(key);
        if (!StringUtils.equals(cacheCode,code)) {
            throw new LyException(ExceptionEnum.INVALID_VERIFY_CODE);
        }
        String encodePwd = encoder.encode(user.getPassword());
        user.setPassword(encodePwd);
        boolean s = this.save(user);
        if (!s) {
            throw new LyException(ExceptionEnum.INSERT_OPERATION_FAIL);
        }
    }

    /**
     * 验证用户名和密码
     * @param username 用户名
     * @param password 密码
     * @return 用户类
     */
    @Override
    public UserDTO queryUser(String username, String password) {
        QueryWrapper<TbUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(TbUser::getUsername,username);

        TbUser tbUser = this.getOne(queryWrapper);
        if (org.springframework.util.StringUtils.isEmpty(tbUser)) {
            throw new LyException(ExceptionEnum.INVALID_USERNAME);
        }
        if (password == null ||!this.encoder.matches(password,tbUser.getPassword())) {
            throw new LyException(ExceptionEnum.INVALID_PASSWORD);
        }
        return BeanHelper.copyProperties(tbUser,UserDTO.class);
    }
}

