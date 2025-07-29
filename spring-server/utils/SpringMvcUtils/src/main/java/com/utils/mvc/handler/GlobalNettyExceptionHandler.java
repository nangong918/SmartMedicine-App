package com.utils.mvc.handler;

import com.czy.api.utils.NettyUtils;
import com.utils.mvc.component.RabbitMqErrorSender;
import exception.ExceptionEnums;
import exception.NettyException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * @author 13225
 * @date 2025/7/29 9:45
 * 全局netty异常拦截
 */
@RequiredArgsConstructor
@ControllerAdvice
public class GlobalNettyExceptionHandler {

    private final RabbitMqErrorSender rabbitMqErrorSender;

    @ExceptionHandler(NettyException.class)
    public void handleNettyException(NettyException e) {
        ExceptionEnums exceptionEnums = new ExceptionEnums() {
            @Override
            public String getCode() {
                return e.getCode();
            }

            @Override
            public String getMessage() {
                return e.getMessage();
            }
        };

        // Mq -> error to receiver
        NettyUtils.sentErrorMessage(
                e.getReceiverId(),
                exceptionEnums,
                rabbitMqErrorSender
        );
    }
}
