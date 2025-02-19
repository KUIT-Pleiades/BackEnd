package com.pleiades.util;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class LocalDateTimeUtil {
    public static LocalDateTime now() {
        return LocalDateTime.now().plusHours(9L);
    }

    public static LocalDate today() {
        return LocalDateTime.now().plusHours(9L).toLocalDate();
    }
}
