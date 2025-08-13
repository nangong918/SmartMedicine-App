package com.utils.common.handler;


/**
 * @author 13225
 * @date 2025/7/29 9:45
 * 全局netty异常拦截
 * 非mvc @ControllerAdvice 无法调用，取消此方法
 * @see com.utils.mvc.aspect.NettyExceptionAspect
 */
/*@RequiredArgsConstructor
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
}*/
