package com.impower.tingshu.common.config.thread;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

/**
 * @classname tingshu-parent
 * @Auther d3Lap1ace
 * @Time 9/8/2024 14:12 周五
 * @description
 * @Version 1.0
 * From the Laplace Demon
 */

@Configuration
public class ThreadPoolConfig {


    @Bean
    public Executor threadPoolExecutor() {
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                availableProcessors * 2,
                availableProcessors * 2,
                0,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(300),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );

        threadPoolExecutor.prestartCoreThread();
        return threadPoolExecutor;

    }
}
