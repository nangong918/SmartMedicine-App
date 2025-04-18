package com.czy.test.component;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author 13225
 * @date 2025/4/14 13:57
 */
@Component
public class ThreadTest {
    public int i1 = 0;
    public volatile int i2 = 0;
    public final AtomicInteger i3 = new AtomicInteger(0);
}
