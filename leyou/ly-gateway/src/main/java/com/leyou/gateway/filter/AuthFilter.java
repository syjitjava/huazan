package com.leyou.gateway.filter;

import com.leyou.common.auth.entity.Payload;
import com.leyou.common.auth.entity.UserInfo;
import com.leyou.common.auth.uilts.JwtUtils;
import com.leyou.common.utils.CookieUtils;
import com.leyou.gateway.config.FilterProperties;
import com.leyou.gateway.config.JwtProperties;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.context.annotation.Configuration;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class AuthFilter extends ZuulFilter {
    @Autowired
    private FilterProperties filterProp;
    private HttpServletRequest getRequest() {
         RequestContext ctx = RequestContext.getCurrentContext();
         return ctx.getRequest();
    }

    /**
     * 过滤形式
     * @return
     */
    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    /**
     * 过滤顺序
     * @return
     */
    @Override
    public int filterOrder() {
        return FilterConstants.FORM_BODY_WRAPPER_FILTER_ORDER - 1;
    }

    /**
     * 是否过滤
     * @return
     */
    @Override
    public boolean shouldFilter() {
        HttpServletRequest request = getRequest();
        String requestURI = request.getRequestURI();
        return !isAllowPath(requestURI);
    }
    private boolean isAllowPath(String requestURI) {
        // 定义一个标记
        boolean flag = false;
        // 遍历允许访问的路径
        for (String path : this.filterProp.getAllowPaths()) {
            // 然后判断是否是符合
            if(requestURI.startsWith(path)){
                flag = true;
                break;
            }
        }
        return flag;
    }
    @Autowired
    private JwtProperties jwtProperties;
    
    public Object run() throws ZuulException {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = getRequest();
        String toKen = CookieUtils.getCookieValue(request, jwtProperties.getUser().getCookieName());
        try {
            Payload<UserInfo> payload = JwtUtils.getInfoFromToken(toKen, jwtProperties.getPublicKey(), UserInfo.class);
            UserInfo user = payload.getUserInfo();
            String role = user.getRole();
            String method = request.getMethod();
            String path = request.getRequestURI();
            log.info("【网关】用户{},角色{}。访问服务{} : {}，", user.getUsername(), role, method, path);
        } catch (Exception e) {
           ctx.setSendZuulResponse(false);
            ctx.setResponseStatusCode(403);
            log.error("非法访问，未登录，地址：{}", request.getRemoteHost(), e );
        }
return null;
    }
}
