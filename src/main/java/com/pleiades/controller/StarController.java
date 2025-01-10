//package com.pleiades.controller;
//
//import com.pleiades.dao.UserDao;
//import com.pleiades.model.StarBackground;
//import com.pleiades.model.User;
//import jakarta.servlet.http.HttpServletRequest;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//
//import java.sql.SQLException;
//import java.util.HashMap;
//import java.util.Map;
//
//@Controller
//public class StarController {
//    // todo: 프론트에 사용자의 id, 닉네임, 배경화면, 캐릭터 전달
//    @GetMapping("/star")
//    public void star(@RequestParam("userId") String userId) throws SQLException {
//        Map<String, Object> result = new HashMap<>();
//        UserRepository userRepository = new UserRepository();
//
//        User user = userRepository.findByUserId(userId);
//
//        if (user == null) {
//            result.put("message", "User not found");
////            return result;
//        }
//
////        StarBackground background =
//
//        result.put("userId", userId);
//        result.put("nickname", user.getNickname());
////        result.put("backgroundName", );
////        result.put("backgroundUrl", );
////        result.put("character", );
//
//    }
//}
