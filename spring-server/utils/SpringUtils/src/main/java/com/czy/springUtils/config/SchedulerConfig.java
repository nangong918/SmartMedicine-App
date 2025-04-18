package com.czy.springUtils.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * @author 13225
 * @date 2025/1/8 14:19
 */
@Configuration
@EnableScheduling
public class SchedulerConfig {
    private static final int CORE_POOL_SIZE = 6;    // 核心线程池大小

    /**
     * 1. 基本概念
     *  这是 Spring 提供的一个类，属于 org.springframework.scheduling.concurrent 包。
     *  它是对 ScheduledThreadPoolExecutor 的封装，提供了更简单的调度任务的 API。
     *  更加集成 Spring 框架的特性，比如支持 Spring 的生命周期管理和依赖注入。
     * <p>
     * 2. 使用场景
     *  适合在 Spring 应用中使用，尤其是需要与其他 Spring 组件协同工作的场景。
     *  提供了更简化的配置方式，适合快速开发。
     * <p>
     * 3. 功能
     *  提供了更高层次的抽象，简化了任务调度的设置。
     *  集成 Spring 的任务调度功能，可以通过注解（如 @Scheduled）轻松定义定时任务。
     * <p>
     * 4. 生命周期管理
     *  可以与 Spring 的生命周期管理集成，当 Spring 容器关闭时，它会自动清理。
     * @return ThreadPoolTaskScheduler
     */
    @Bean(name = "taskScheduler")
    public TaskScheduler taskScheduler(){
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(CORE_POOL_SIZE);
        taskScheduler.initialize();
        return taskScheduler;
    }

    /**
     * 1. 基本概念
     *  这是 Java 提供的一个类，属于 java.util.concurrent 包。
     *  它是一个线程池，可以用于执行定时任务。
     *  适用于需要精确控制线程池行为的场景。
     *  <p>
     * 2. 使用场景
     *  适用于需要高性能和低延迟的任务调度。
     *  适合需要直接使用 Java 并发工具的场景。
     *  <p>
     * 3. 功能
     *  提供了更底层的 API，允许对线程池的大小、任务队列等进行更细粒度的控制。
     *  可以直接使用 schedule() 和 scheduleAtFixedRate() 等方法来调度任务。
     *  <p>
     * 4. 生命周期管理
     *  需要手动管理生命周期，调用 shutdown() 来停止线程池。
     * @return ScheduledThreadPoolExecutor
     */
    @Bean(name = "scheduledTaskExecutor", destroyMethod = "shutdown")
    public ScheduledThreadPoolExecutor scheduledTaskExecutor() {
        ScheduledThreadPoolExecutor  executor = new ScheduledThreadPoolExecutor(CORE_POOL_SIZE);
        // 取消任务时移除
        executor.setRemoveOnCancelPolicy(true);
        return executor;
    }
}
