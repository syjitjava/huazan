package com.leyou.order.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

@Component
public class FeignInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {
        if (!requestTemplate.path().contains("address")) {
            return ;
        }
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        //获得request对象
        HttpServletRequest request = requestAttributes.getRequest();
        //获取头内所有内容
        Enumeration<String> headerNames = request.getHeaderNames();
        //遍历头信息
        while(headerNames.hasMoreElements()){
            //头参数的名字
            String name = headerNames.nextElement();
            if(name.equals("cookie")){
                //把cookie内容放入feign的请求头中，名字 是：cookie
                String headerValue = request.getHeader(name);
                System.out.println(headerValue);
                //把头信息放入 requestTemplate中
                requestTemplate.header(name,headerValue);
            }
        }
    }
}
