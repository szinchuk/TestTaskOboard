package com.task.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Slf4j
public class ThreadPoolService {

    private final ExecutorService executorService;

    public ThreadPoolService() {
        this.executorService = Executors.newFixedThreadPool(
                Math.min(10, Runtime.getRuntime().availableProcessors() * 2),
                new ThreadFactory() {
                    private final ThreadFactory delegate = Executors.defaultThreadFactory();
                    private int counter = 0;

                    @Override
                    public Thread newThread(Runnable runnable) {
                        Thread thread = delegate.newThread(runnable);
                        thread.setName("joke-fetcher-" + counter++);
                        return thread;
                    }
                });

        log.info("ThreadPoolService initialized with {} threads",
                Math.min(10, Runtime.getRuntime().availableProcessors() * 2));
    }

    public <T> CompletableFuture<T> submitTask(Supplier<T> task) {
        return CompletableFuture.supplyAsync(task, executorService);
    }

    public <T> List<T> executeInParallel(List<Supplier<T>> tasks) {
        List<CompletableFuture<T>> futures = tasks.stream()
                .map(this::submitTask)
                .collect(Collectors.toList());

        return futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
    }

    public <T> List<T> executeRepeatedTaskInParallel(Supplier<T> task, int times) {
        List<Supplier<T>> tasks = IntStream.range(0, times)
                .mapToObj(i -> task)
                .collect(Collectors.toList());

        return executeInParallel(tasks);
    }

    public <T> List<T> executeInBatches(Supplier<T> task, int totalTasks, int batchSize) {
        int fullBatches = totalTasks / batchSize;
        int remainder = totalTasks % batchSize;

        List<T> results = new CopyOnWriteArrayList<>();

        // Выполняем полные пакеты
        for (int i = 0; i < fullBatches; i++) {
            List<T> batchResults = executeRepeatedTaskInParallel(task, batchSize);
            results.addAll(batchResults);
            log.debug("Completed batch {} of {}", i + 1, fullBatches);
        }

        // Выполняем оставшиеся задачи
        if (remainder > 0) {
            List<T> remainingResults = executeRepeatedTaskInParallel(task, remainder);
            results.addAll(remainingResults);
            log.debug("Completed remainder batch with {} tasks", remainder);
        }

        return results;
    }

    public void shutdown() {
        log.info("Shutting down ThreadPoolService");
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                log.warn("ThreadPool did not terminate in time, forcing shutdown");
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            log.error("ThreadPool shutdown was interrupted", e);
            Thread.currentThread().interrupt();
        }
    }
}