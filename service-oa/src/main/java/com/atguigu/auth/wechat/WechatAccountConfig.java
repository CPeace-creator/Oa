package com.atguigu.auth.wechat;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author cjh
 * @date 2023/11/9
 */
@Data
@Component
@ConfigurationProperties(prefix = "wechat")
public class WechatAccountConfig {
    private String mpAppId;

    private String mpAppSecret;

}
