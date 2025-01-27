package com.pleiades.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashStringUtil {
    public static String hashString(String input) {
        try {
            // MessageDigest 인스턴스 생성 (SHA-256 알고리즘)
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // 입력 문자열을 해시 처리
            byte[] hashBytes = digest.digest(input.getBytes());

            // 해시 바이트 배열을 16진수 문자열로 변환
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b); // 0xff로 마스킹해 양수로 변환
                if (hex.length() == 1) {
                    hexString.append('0'); // 한 자리일 경우 앞에 0 추가
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error: SHA-256 algorithm not found", e);
        }
    }

}
