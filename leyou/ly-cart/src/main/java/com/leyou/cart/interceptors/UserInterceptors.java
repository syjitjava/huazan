package com.leyou.cart.interceptors;

import com.leyou.common.auth.entity.Payload;
import com.leyou.common.auth.entity.UserInfo;
import com.leyou.common.auth.uilts.JwtUtils;
import com.leyou.common.threadlocals.UserHolder;
import com.leyou.common.utils.CookieUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
@Slf4j
public class UserInterceptors implements HandlerInterceptor {
    private static final String COOKIE_NAME = "LY_TOKEN";
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
            String token = CookieUtils.getCookieValue(request, COOKIE_NAME);
            Payload<UserInfo> payload = JwtUtils.getInfoFromToken(token, UserInfo.class);
            Long id = payload.getUserInfo().getId();
            UserHolder.setTl(id);
           // log.info("【购物车服务】解析用户信息成功");
            return true;
        } catch (UnsupportedEncodingException e) {
            log.error("【购物车服务】解析用户信息失败",e);
        }
        return false;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
          UserHolder.removeUser();
    }
}
