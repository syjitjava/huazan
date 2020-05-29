package com.leyou.user.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.security.SecureRandom;

@Data
@Configuration
@ConfigurationProperties(prefix = "ly.encoder.crypt")
public class PasswordEncoder {

    private String secret;
    private Integer strength;
    @Bean
    public BCryptPasswordEncoder encoder(){
       return new BCryptPasswordEncoder(strength,new SecureRandom(secret.getBytes()));
    }
}
