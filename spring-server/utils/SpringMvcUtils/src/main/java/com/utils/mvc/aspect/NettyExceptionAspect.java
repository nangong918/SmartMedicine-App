package com.utils.mvc.aspect;

/**
 * @author 13225
 * @date 2025/7/30 11:15
 * 不建议这样写。因为AOP会在每个方法调用之后进行校验。
 * 全局异常拦截只有在出现异常才进行异常检查，所以这样每个方法都进行检查会出现额外的开销
 */
/*@Slf4j
@Aspect
@RequiredArgsConstructor
@Component
public class NettyExceptionAspect {

    // 拦截所有类中的所有方法抛出的 AopException
    @AfterThrowing(pointcut = "execution(* *(..))", throwing = "ex")
    public void handleAopException(NettyException ex) {
        // 处理异常逻辑
        System.err.println("捕获到 AopException: " + ex.getMessage());

        // 这里可以添加其他处理逻辑，比如记录日志、发送通知等
    }

}*/
