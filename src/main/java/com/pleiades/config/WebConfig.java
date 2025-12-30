package com.pleiades.config;

import com.pleiades.interceptor.AuthInterceptor;
import com.pleiades.interceptor.StationAuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${FRONT_ORIGIN}")
    private String FRONT_ORIGIN;

    private final AuthInterceptor authInterceptor;
    private final StationAuthInterceptor stationAuthInterceptor;

    @Autowired
    public WebConfig(AuthInterceptor authInterceptor, StationAuthInterceptor stationAuthInterceptor) {
        this.authInterceptor = authInterceptor;
        this.stationAuthInterceptor = stationAuthInterceptor;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 모든 경로에 대해
                .allowedOrigins(FRONT_ORIGIN, "http://localhost:5173")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 인터셉터 등록
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**")  // 모든 경로에 대해 인터셉터 적용
                .excludePathPatterns(
                        "/auth/refresh",
                        "/error",
                        "/auth/login/**",
                        "/redis/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html/**",
                        "/v3/api-docs/**"
//                        "/auth/login",
//                        "/auth/login/",
//                        "/auth/login/naver"
                        );  // 제외할 경로

        registry.addInterceptor(stationAuthInterceptor)
                .addPathPatterns("/stations/**")
                .excludePathPatterns("/stations");
    }
}

