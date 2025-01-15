package com.pleiades.strings;

public enum KakaoUrl {
    AUTH_URL("https://kauth.kakao.com/oauth/authorize"),
    KAKAO_CLIENT_ID("1c2f6bdd53dbadbd6301ef5075764d16"),
    REDIRECT_URI("http://54.252.108.194:8080/auth/login/kakao/callback"),
    TOKEN_URL("https://kauth.kakao.com/oauth/token"),
    USER_INFO_URL("https://kapi.kakao.com/v2/user/me"),
    ACCESS_TOKEN_INFO_URL("https://kapi.kakao.com/v1/user/access_token_info"),
    TOKEN_REFRESH_URL("https://kauth.kakao.com/oauth/token");

    private final String path;

    KakaoUrl(String path) {
        this.path = path;
    }

    public String getUrl() {
        return path;
    }
}