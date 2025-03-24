package com.task.config;

import com.task.service.ThreadPoolService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApplicationShutdownHandler implements ApplicationListener<ContextClosedEvent> {

    private final ThreadPoolService threadPoolService;

    @Override
    public void onApplicationEvent(@NonNull ContextClosedEvent event) {
        log.info("Application shutdown detected, cleaning up resources");
        threadPoolService.shutdown();
    }
}