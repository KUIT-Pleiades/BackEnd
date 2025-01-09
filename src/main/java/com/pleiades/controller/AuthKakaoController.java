package com.pleiades.controller;

import com.pleiades.dao.UserDao;
import com.pleiades.dto.KakaoAccountDto;
import com.pleiades.dto.KakaoTokenDto;
import com.pleiades.dto.KakaoUserDto;
import com.pleiades.model.User;
import com.pleiades.util.KakaoRequest;
import com.pleiades.strings.KakaoUrl;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/auth")
public class AuthKakaoController {

    @GetMapping("/login/kakao")
    public void loginRedirect(HttpServletResponse response) throws IOException {
        String redirectUrl = KakaoUrl.AUTH_URL.getUrl() +
                "?response_type=code" +
                "&client_id=" + KakaoUrl.KAKAO_CLIENT_ID.getUrl() +
                "&redirect_uri=" + KakaoUrl.REDIRECT_URI.getUrl();

        response.sendRedirect(redirectUrl);
    }

    //todo: 서버 엔드포인트
    @GetMapping("/code/kakao")
    public void getAccess(@RequestParam("code") String code, HttpServletResponse response) throws SQLException, IOException {
        Map<String, Object> result = new HashMap<>();

        ResponseEntity<KakaoTokenDto> responseToken = KakaoRequest.postAccessToken(code);
        ResponseEntity<KakaoUserDto> responseUser = null;
        KakaoAccountDto account = null; String email = null;

        if (responseToken != null) { responseUser = KakaoRequest.postUserEmail(responseToken.getBody().getAccessToken()); }
        if (responseUser != null) { account = responseUser.getBody().getKakaoAccount(); }
        if (account != null) { email = account.getEmail(); }

        UserDao userDao = new UserDao();

        User user = userDao.findBySocialId(email);

        if (user != null) { response.sendRedirect("/star?userId=" + user.getUserId()); return;}
        response.sendRedirect("/auth/signup");
    }

//    @GetMapping("/signup")
//    public String signup() {
//
//    }
}