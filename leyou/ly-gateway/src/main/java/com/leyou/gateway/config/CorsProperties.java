package com.leyou.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.swing.*;
import java.util.List;

@Data
@ConfigurationProperties(prefix = "ly.cors")
public class CorsProperties {

    private List<String> addAllowedOrigin;

    private Boolean allowCredentials;

    private List<String> addAllowedMethod;

    private List<String> allowedHeaders;

    private Long maxAge;

    private String filterPath;
}
