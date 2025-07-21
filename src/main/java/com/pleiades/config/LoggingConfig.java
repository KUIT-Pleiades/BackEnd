package com.pleiades.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.stream.Collectors;

@Aspect
@Component
@Slf4j
public class LoggingConfig {

    @Before("execution(* com.pleiades.controller.*.*(..))")
    public void controllerMethodInfo(JoinPoint joinPoint) {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        String arguments = Arrays.stream(args)
                .map(arg -> arg != null? arg.toString() : "null")
                .collect(Collectors.joining(", "));

        log.info("\nCLASS: {}\nMETHOD: {}\nARGUMENTS : {}", className, methodName, arguments);
    }

    @AfterReturning(pointcut = "execution(* com.pleiades.controller.*.*(..))", returning = "result")
    public void controllerShowResponse(JoinPoint joinPoint, Object result) {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        String response = result != null ? result.toString() : "null";

        if (result instanceof ResponseEntity<?>) {
            ResponseEntity<?> responseEntity = (ResponseEntity<?>) result;

            responseEntityLog(className, methodName, responseEntity);
            return;
        }

        log.info("\nCLASS: {}\nMETHOD NAME: {}\nRESPONSE: {}", className, methodName, response);
    }

    private void responseEntityLog(String className, String methodName, ResponseEntity<?> responseEntity) {
        String statusCode = responseEntity.getStatusCode().toString();
        String headers = responseEntity.getHeaders().toString();
        String body = responseEntity.getBody() == null? "null" : responseEntity.getBody().toString();

        log.info("\nCLASS: {}\nMETHOD NAME: {}\nRESPONSE\n\tstatus - {}\n\theader - {}\n\tbody - {}",
                className, methodName, statusCode, headers, body);
    }

}
