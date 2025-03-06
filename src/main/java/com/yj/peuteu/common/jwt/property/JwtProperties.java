package com.yj.peuteu.common.jwt.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    private String secret;
    private String prefix;
    private String claim;
    private JwtAccessProperties access;
    private JwtRefreshProperties refresh;

    @Getter @Setter
    public static class JwtAccessProperties {
        private int age;
        private String header;
        private String subject;
    }

    @Getter @Setter
    public static class JwtRefreshProperties {
        private int age;
        private int minAgeInDay;
        private String subject;
    }
}
