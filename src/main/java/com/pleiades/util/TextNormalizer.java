package com.pleiades.util;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TextNormalizer {

    /** 쉼표로 분리 + trim + 빈값 제거 */
    public static List<String> splitCsv(String csv) {
        if (csv == null || csv.isBlank()) return List.of();
        return Arrays.stream(csv.split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .collect(Collectors.toList());
    }

    /** 영문 모두 소문자, 공백 정리 */
    public static String norm(String s) {
        if (s == null) return "";
        return s.trim()
                .replaceAll("\\s+", " ")
                .toLowerCase();
    }

    /** 정규화 후 동등 비교 */
    public static boolean eq(String a, String b) {
        return norm(a).equals(norm(b));
    }
}
