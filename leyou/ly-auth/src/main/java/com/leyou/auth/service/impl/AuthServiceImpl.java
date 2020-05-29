package com.leyou.auth.service.impl;

import com.leyou.auth.config.JwtProperties;
import com.leyou.auth.service.AuthService;
import com.leyou.common.auth.entity.Payload;
import com.leyou.common.auth.entity.UserInfo;
import com.leyou.common.auth.uilts.JwtUtils;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.CookieUtils;
import com.leyou.user.client.AuthClient;
import com.leyou.user.dto.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    private AuthClient authClient;
    @Autowired
    private JwtProperties jwt;
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public void login(String username, String password,HttpServletResponse response) {
        UserDTO userDTO = null;
        try {
            userDTO = authClient.queryUser(username, password);
        } catch (Exception e) {
            throw new LyException(ExceptionEnum.INVALID_USERNAME_PASSWORD);
        }
        if (StringUtils.isEmpty(userDTO)) {
            throw new LyException(ExceptionEnum.INVALID_USERNAME_PASSWORD);
        }
        try {

            UserInfo userInfo = new UserInfo(userDTO.getId(),userDTO.getUsername(),"admin");

            String token = JwtUtils.generateTokenExpireInMinutes(userInfo, jwt.getPrivateKey(), jwt.getUser().getExpire());
            CookieUtils.newCookieBuilder()
                    .name(jwt.getUser().getCookieName())
                    .domain(jwt.getUser().getCookieDomain())
                    .value(token)
                    .response(response)
                    .httpOnly(true)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("解析秘钥时发生异常！！！！");
        }

    }

    /**
     * 验证用户登录
     * @param request
     * @return
     */
    @Override
    public UserInfo verify(HttpServletRequest request,HttpServletResponse response) {
        Payload<UserInfo> payload = null;
        try {
            String cookieValue = CookieUtils.getCookieValue(request, jwt.getUser().getCookieName());
            payload = JwtUtils.getInfoFromToken(cookieValue, jwt.getPublicKey(), UserInfo.class);
            String payloadId = payload.getId();
            Boolean aBoolean = redisTemplate.hasKey(payloadId);
            if (aBoolean !=null && aBoolean) {
                throw new LyException(ExceptionEnum.UNAUTHORIZED);
            }
            Date expiration = payload.getExpiration();
            DateTime dateTime = new DateTime(expiration.getTime()).minusMinutes(jwt.getUser().getMinRefreshInterval());
            if (dateTime.isBefore(System.currentTimeMillis())) {
                // 如果过了刷新时间，则生成新token
                String token = JwtUtils.generateTokenExpireInMinutes(payload.getUserInfo(), jwt.getPrivateKey(), jwt.getUser().getExpire());
                // 写入cookie
                CookieUtils.newCookieBuilder()
                        // response,用于写cookie
                        .response(response)
                        // 保证安全防止XSS攻击，不允许JS操作cookie
                        .httpOnly(true)
                        // 设置domain
                        .domain(jwt.getUser().getCookieDomain())
                        // 设置cookie名称和值
                        .name(jwt.getUser().getCookieName()).value(token)
                        // 写cookie
                        .build();

            }
        } catch (Exception e) {
            throw  new LyException(ExceptionEnum.UNAUTHORIZED);
        }
        return payload.getUserInfo();
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        String id =null;
        try {
            String token = CookieUtils.getCookieValue(request, jwt.getUser().getCookieName());
            Payload<UserInfo> payload = JwtUtils.getInfoFromToken(token, jwt.getPublicKey(), UserInfo.class);
             id = payload.getId();
            long time = payload.getExpiration().getTime() - System.currentTimeMillis();
            if (time > 5000L) {
                 redisTemplate.opsForValue().set(id,"1",time, TimeUnit.MINUTES);
            }

            CookieUtils.deleteCookie(jwt.getUser().getCookieName(),jwt.getUser().getCookieDomain(),response);
        } catch (Exception e) {
            log.error("注销操作失败"+id);
            throw  new LyException(ExceptionEnum.valueOf("退出登录失败"));
        }
    }
}
