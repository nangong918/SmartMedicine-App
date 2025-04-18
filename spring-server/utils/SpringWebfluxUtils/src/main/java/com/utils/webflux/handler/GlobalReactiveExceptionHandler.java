package com.utils.webflux.handler;


import exception.AppException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice // 使用 WebFlux 专用注解
public class GlobalReactiveExceptionHandler {

    // 自定义业务异常处理
    @ExceptionHandler(AppException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleAppException(AppException ex) {

        Map<String, Object> body = new HashMap<>();
        body.put("code", ex.getErrCode());
        body.put("message", ex.getMessage());

        return Mono.just(
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body)
        );
    }

    // 入参校验：SpringMVC校验失败抛出异常
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Mono<ResponseEntity<Map<String, String>>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> body = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                body.put(error.getField(), error.getDefaultMessage())
        );
        return Mono.just(
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body)
        );
    }

    // 入参校验：Spring Reactive校验失败抛出异常
    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<Map<String, String>>> handleWebExchangeBindException(WebExchangeBindException ex) {
        Map<String, String> body = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                body.put(error.getField(), error.getDefaultMessage())
        );
        return Mono.just(
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body)
        );
    }

    // 全局兜底异常处理
    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleGlobalException(Exception ex) {

        Map<String, Object> body = new HashMap<>();
        body.put("code", "INTERNAL_ERROR");
        body.put("message", "服务器开小差了，请稍后再试");

        // 生产环境应记录完整错误日志
        log.error("Error processing request", ex);

        return Mono.just(
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body)
        );
    }
}