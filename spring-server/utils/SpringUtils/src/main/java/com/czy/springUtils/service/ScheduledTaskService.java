package com.czy.springUtils.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author 13225
 * @date 2025/1/8 13:36
 * 定时任务
 * TODO 研究@Async，研究ScheduledThreadPoolExecutor和TaskScheduler ; 尝试解耦：ScheduledTaskService单一职责：只执行定时任务的工具类，定时任务通过Runnable加载到内部
 */
@Slf4j
@EnableScheduling
// @EnableScheduling与WebSocket冲突，会导致Bean named 'defaultSockJsTaskScheduler' is expected to be of type 'org.springframework.scheduling.TaskScheduler' but was actually of type 'org.springframework.beans.factory.support.NullBean'
@Service
public class ScheduledTaskService {


    // @Scheduled与WebSocket冲突，会导致Bean named 'defaultSockJsTaskScheduler' is expected to be of type 'org.springframework.scheduling.TaskScheduler' but was actually of type 'org.springframework.beans.factory.support.NullBean'
//    @Scheduled(fixedRate = 1000L * 60 * 60 * 24) // 每隔一天执行一次
//    public void functionTest12EveryDay() {
//        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Shanghai"));
//        log.info("@Scheduled定时任务：，当前时间： {}", now);
//    }

    @PostConstruct
    public void init() {
    }

    // 定时任务订阅对象
    private final ScheduledFuture<?> everyDayTestScheduledFuture = null;

    @Qualifier("scheduledTaskExecutor")
    @Autowired
    private ScheduledThreadPoolExecutor schedulerThreadPoolExecutor;

    @Qualifier("taskScheduler")
    @Autowired
    private TaskScheduler taskScheduler;

    /**
     * 提交定时任务 Runnable，Time
     * @param task           任务
     * @param initialDelay   初始延迟
     * @param unit           时间单位
     */
    public void submitTask(Runnable task, long initialDelay, TimeUnit unit) {
        schedulerThreadPoolExecutor.schedule(task, initialDelay, unit);
    }

    /**
     * 提交循环执行的定时任务
     * @param task           任务
     * @param initialDelay   初始延迟
     * @param period         执行周期
     * @param unit           时间单位
     */
    public void submitRepeatingTask(Runnable task, long initialDelay, long period, TimeUnit unit) {
        schedulerThreadPoolExecutor.scheduleAtFixedRate(task, initialDelay, period, unit);
    }

    /**
     * 提交循环执行的定时任务（以固定延迟执行）
     * @param task           任务
     * @param initialDelay   初始延迟
     * @param delay          延迟时间
     * @param unit           时间单位
     */
    public void submitTaskWithFixedDelay(Runnable task, long initialDelay, long delay, TimeUnit unit) {
        schedulerThreadPoolExecutor.scheduleWithFixedDelay(task, initialDelay, delay, unit);
    }


    @PreDestroy
    public void cleanup() {
        schedulerThreadPoolExecutor.shutdown();
    }
}
