//package com.pleiades.controller;
//
//
//import com.pleiades.entity.StarBackground;
//import com.pleiades.entity.User;
//import com.pleiades.repository.UserRepository;
//import jakarta.servlet.http.HttpServletRequest;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//
//import java.sql.SQLException;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Optional;
//
//@Controller
//public class StarController {
//    @Autowired
//    UserRepository userRepository;
//
//    // todo: 프론트에 사용자의 id, 닉네임, 배경화면, 캐릭터 전달
//    @GetMapping("/star")
//    public ResponseEntity<Map<String, String>> star(@RequestParam("userId") String userId) throws SQLException {
//        Map<String, String> result = new HashMap<>();
//
//        Optional<User> user = userRepository.findById(userId);
//
//        if (!user.isPresent()) {
//            result.put("message", "User not found");
//            return ResponseEntity
//                    .status(HttpStatus.OK)
//                    .body(result);
//        }
//
//        StarBackground background = Star
//
//        result.put("userId", userId);
//        result.put("nickname", user.get().getUserName());
//        result.put("backgroundName", );
//        result.put("backgroundUrl", );
//        result.put("character", );
//
//    }
//}
