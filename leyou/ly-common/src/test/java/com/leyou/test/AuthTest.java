package com.leyou.test;

import com.leyou.common.auth.entity.Payload;
import com.leyou.common.auth.entity.UserInfo;
import com.leyou.common.auth.uilts.JwtUtils;
import com.leyou.common.auth.uilts.RsaUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.security.PrivateKey;
import java.security.PublicKey;

@Slf4j
public class AuthTest {
    private String privateFilepath = "D:\\workspace\\leyou\\ly-common\\ssh\\id_rsa";

    private String publicFilepath = "D:\\workspace\\leyou\\ly-common\\ssh\\id_rsa.pub";

    @Test
    public void authTest() throws Exception {
        RsaUtils.generateKey( publicFilepath, privateFilepath, "hello", 2048);
    }
    @Test
    public void getAuthTest() throws Exception {
        PublicKey publicKey = RsaUtils.getPublicKey(publicFilepath);
        System.out.println(publicKey);
        PrivateKey privateKey = RsaUtils.getPrivateKey(privateFilepath);
        System.out.println(privateKey);
    }

    @Test
    public void jwtTest() throws Exception {
        PublicKey publicKey = RsaUtils.getPublicKey(publicFilepath);
        PrivateKey privateKey = RsaUtils.getPrivateKey(privateFilepath);

        UserInfo userInfo = new UserInfo(1L, "tomqqqq", "admin");
        String token = JwtUtils.generateTokenExpireInMinutes(userInfo, privateKey, 10);
        log.info("token:"+token);

        Payload<? extends UserInfo> info = JwtUtils.getInfoFromToken(token, publicKey, userInfo.getClass());
        log.info(info.getId());
        log.info(String.valueOf(info.getUserInfo()));
        log.info(String.valueOf(info.getExpiration()));
    }
}
