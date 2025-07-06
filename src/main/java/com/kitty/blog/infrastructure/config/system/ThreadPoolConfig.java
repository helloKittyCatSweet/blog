package com.kitty.blog.infrastructure.config.system;

import cn.hutool.core.thread.ThreadFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class ThreadPoolConfig {

    @Bean("analyticsExecutor")
    public ThreadPoolExecutor analyticsExecutor() {
        return new ThreadPoolExecutor(
                5,                      // 核心线程数
                10,                     // 最大线程数
                60L,                    // 空闲线程存活时间
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(100),  // 工作队列
                new ThreadFactoryBuilder()
                        .setNamePrefix("analytics-pool-")
                        .build(),
                new ThreadPoolExecutor.CallerRunsPolicy()  // 拒绝策略
        );
    }

    @Bean("searchExecutor")
    public ThreadPoolExecutor searchExecutor() {
        return new ThreadPoolExecutor(
                3,                      // 核心线程数
                6,                      // 最大线程数
                60L,                    // 空闲线程存活时间
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(50),   // 工作队列
                new ThreadFactoryBuilder()
                        .setNamePrefix("search-pool-")
                        .build(),
                new ThreadPoolExecutor.CallerRunsPolicy()  // 拒绝策略
        );
    }
}