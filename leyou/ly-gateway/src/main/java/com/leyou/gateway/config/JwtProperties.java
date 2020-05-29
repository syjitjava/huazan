package com.leyou.gateway.config;

import com.leyou.common.auth.uilts.RsaUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.security.PrivateKey;
import java.security.PublicKey;

@Slf4j
@Data
@ConfigurationProperties("ly.jwt")
public class JwtProperties implements InitializingBean {

    private String pubKeyPath;

    private PublicKey publicKey;

    private UserProper user = new UserProper();
    @Data
    public class UserProper{
        /**
         * 存放token的cookie名称
         */
        private String cookieName;
    }

    public void afterPropertiesSet(){
        try {
            publicKey = RsaUtils.getPublicKey(pubKeyPath);
        } catch (Exception e) {
            log.error("初始化公钥失败。。。");
            throw new RuntimeException("初始化公钥失败。。。");
        }
    }
}
