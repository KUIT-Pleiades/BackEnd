package com.pleiades.util;

import org.springframework.stereotype.Component;

@Component
public class HeaderUtil {
    public static String authorizationBearer(String header) {
        return header.substring(header.indexOf("Bearer ") + 7);
    }
}
