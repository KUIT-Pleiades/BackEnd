//package com.pleiades.model;
//
//import io.swagger.v3.core.util.Json;
//import jakarta.servlet.http.Cookie;
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import lombok.Setter;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@Getter
//@Setter
//public class JsonResponseBuilder<T> {
//    static HttpHeaders headers;
//    Map<String, T> body;
//
//    private JsonResponseBuilder(HttpHeaders headers, Map<String, T> body) {
//        this.headers = headers;
//        this.body = body;
//    }
//
//    public static JsonResponseBuilder of() {
//        headers = new HttpHeaders();
//        body = new HashMap<>();
//
//        headers.setContentType(MediaType.APPLICATION_JSON);
//
//        return new JsonResponseBuilder(headers, body);
//    }
//
//    public void setCookie(Cookie cookie) {
//        headers.add(HttpHeaders.SET_COOKIE, String.format(
//                "%s=%s; Path=%s; HttpOnly; Max-Age=%d; %sSameSite=Strict",
//                cookie.getName(),
//                cookie.getValue(),
//                cookie.getPath(),
//                cookie.getMaxAge(),
//                cookie.getSecure() ? "Secure; " : ""
//        ));
//    }
//
//    public void putAccessToken(T accessToken) {
//        body.put("accessToken", accessToken);
//    }
//
//
//
//
//
//}
