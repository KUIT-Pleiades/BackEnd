package com.pleiades.controller;

import com.pleiades.service.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

@Slf4j
@Controller
@RequestMapping("/auth")
public class AuthHomeController {
    private final JwtUtil jwtUtil = new JwtUtil();

    @PostMapping("/")
    public void login(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String token = request.getParameter("token");
        if (token != null) {
            response.sendRedirect("/auth/login");
            return;
        }
        String userId = jwtUtil.validateToken(token).getId();
        response.sendRedirect("/star?userId=" + userId);
    }

    @GetMapping("/login")
    public void socialLogin(HttpServletResponse response) {

    }

    @GetMapping("/signup")
    public ResponseEntity<String> signup(HttpServletRequest request, HttpServletResponse response) {
        log.info("signup");

        String body = "<!DOCTYPE html>\n" +
                "<html lang=\"ko\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>회원가입</title>\n" +
                "    <link rel=\"stylesheet\" href=\"styles.css\">\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"container\">\n" +
                "        <h2>회원가입</h2>\n" +
                "        <form action=\"/signup\" method=\"POST\">\n" +
                "            <div class=\"form-group\">\n" +
                "                <label for=\"username\">아이디</label>\n" +
                "                <input type=\"text\" id=\"username\" name=\"username\" required>\n" +
                "            </div>\n" +
                "            <div class=\"form-group\">\n" +
                "                <label for=\"email\">이메일</label>\n" +
                "                <input type=\"email\" id=\"email\" name=\"email\" required>\n" +
                "            </div>\n" +
                "            <div class=\"form-group\">\n" +
                "                <label for=\"password\">비밀번호</label>\n" +
                "                <input type=\"password\" id=\"password\" name=\"password\" required>\n" +
                "            </div>\n" +
                "            <div class=\"form-group\">\n" +
                "                <label for=\"confirmPassword\">비밀번호 확인</label>\n" +
                "                <input type=\"password\" id=\"confirmPassword\" name=\"confirmPassword\" required>\n" +
                "            </div>\n" +
                "            <button type=\"submit\">회원가입</button>\n" +
                "        </form>\n" +
                "        <p>이미 계정이 있으신가요? <a href=\"/login\">로그인</a></p>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>\n";

        return ResponseEntity.ok(body);
    }
}
