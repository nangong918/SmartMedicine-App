package com.czy.springUtils.config;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author 13225
 * @date 2024/12/18 14:18
 */

// TODO 配置线程池：线程池过大，切换出现问题，过小，等待出现问题
@Configuration
public class ThreadConfig {

    // 线程池相关配置
    // 核心线程池大小
    private static final int CORE_POOL_SIZE = 8;
    // 最大线程池大小
    private static final int MAX_POOL_SIZE = 80;
    // 任务队列容量
    private static final int QUEUE_CAPACITY = 250;
    // 线程空闲时间（秒）
    private static final int KEEP_ALIVE_SECONDS = 300;


    /**
     * 创建线程池
     * <p>
     * 注解@Bean: 表示这个方法返回的对象将被 Spring 容器管理，并作为一个 Bean 注册
     * <p>
     * name = "taskExecutor": 指定 Bean 的名称为 taskExecutor。
     * <p>
     * destroyMethod = "shutdown": 当 Spring 容器关闭时，会调用这个方法来进行清理
     * <p>
     * @return  线程池
     */
    @Bean(name = "globalTaskExecutor", destroyMethod = "shutdown")
    public ThreadPoolTaskExecutor globalTaskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        //线程池维护线程的最少数量
        taskExecutor.setCorePoolSize(CORE_POOL_SIZE);
        //线程池维护线程的最大数量
        taskExecutor.setMaxPoolSize(MAX_POOL_SIZE);
        //线程池所使用的缓冲队列
        taskExecutor.setQueueCapacity(QUEUE_CAPACITY);
        //线程池维护线程所允许的空闲时间
        taskExecutor.setKeepAliveSeconds(KEEP_ALIVE_SECONDS);
        // 设置线程池在关闭时等待正在执行的任务完成后再关闭
        taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        // 设置任务装饰器，可以在任务执行前后进行额外处理
        taskExecutor.setTaskDecorator(new TaskDecorator() {
            @NotNull
            @Override
            public Runnable decorate(@NotNull Runnable runnable) {
                // 可以做额外处理 eg：org.slf4j.MDC
                return runnable;
            }
        });
        return taskExecutor;
    }

    @Bean(name = "ruleExecutor", destroyMethod = "shutdown")
    public ThreadPoolTaskExecutor ruleExecutor() {
        ThreadPoolTaskExecutor ruleExecutor = new ThreadPoolTaskExecutor();
        // 线程池维护线程的最少数量
        ruleExecutor.setCorePoolSize(CORE_POOL_SIZE);
        // 线程池维护线程的最大数量
        ruleExecutor.setMaxPoolSize(MAX_POOL_SIZE);
        // 线程池所使用的缓冲队列
        ruleExecutor.setQueueCapacity(QUEUE_CAPACITY);
        // 线程池维护线程所允许的空闲时间
        ruleExecutor.setKeepAliveSeconds(KEEP_ALIVE_SECONDS);
        // 设置为守护线程 意味着如果所有非守护线程结束，JVM 可以退出
        ruleExecutor.setDaemon(true);
        // 设置拒绝策略为 AbortPolicy，当线程池无法接受新任务时抛出异常
        ruleExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        // 设置线程池在关闭时等待正在执行的任务完成后再关闭
        ruleExecutor.setWaitForTasksToCompleteOnShutdown(true);
        // 设置线程工厂，指定线程名称的前缀为 ruleService-exec
        ruleExecutor.setThreadFactory(new CustomizableThreadFactory("ruleService-exec"));
        return ruleExecutor;
    }
}
