package com.pleiades.strings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

public enum KakaoUrl {
    AUTH_URL("https://kauth.kakao.com/oauth/authorize"),
    REDIRECT_URI("https://SERVER_DOMAIN/auth/login/kakao/callback"),
    TOKEN_URL("https://kauth.kakao.com/oauth/token"),
    USER_INFO_URL("https://kapi.kakao.com/v2/user/me"),
    ACCESS_TOKEN_INFO_URL("https://kapi.kakao.com/v1/user/access_token_info"),
    TOKEN_REFRESH_URL("https://kauth.kakao.com/oauth/token");

    private static final Logger log = LoggerFactory.getLogger(KakaoUrl.class);
    private final String path;

    KakaoUrl(String path) {
        this.path = path;
    }

    public String getUrl() {
        if (this == REDIRECT_URI) {
            return null;
        }
        return path;
    }

    public String getRedirectUri(String SERVER_DOMAIN) {
        if (this != REDIRECT_URI) {
            return null;
        }
        log.info("server domain: {}", SERVER_DOMAIN);
        return "https://"+ SERVER_DOMAIN +"/auth/login/kakao/callback";
    }
}