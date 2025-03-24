package com.task.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ThreadPoolServiceTest {

    private ThreadPoolService threadPoolService = new ThreadPoolService();

    @Test
    public void testSubmitTask() throws Exception {
        String result = threadPoolService.submitTask(() -> "test").get();
        assertEquals("test", result);
    }

    @Test
    public void testExecuteInParallel() {
        List<Integer> results = threadPoolService.executeRepeatedTaskInParallel(() -> 1, 5);

        assertEquals(5, results.size());
        assertTrue(results.stream().allMatch(i -> i == 1));
    }

    @Test
    public void testExecuteInBatches() {
        AtomicInteger counter = new AtomicInteger(0);

        List<Integer> results = threadPoolService.executeInBatches(
                counter::incrementAndGet, 15, 5);

        assertEquals(15, results.size());
        assertEquals(15, counter.get());
    }

    @Test
    public void testExecuteZeroTasks() {
        List<String> results = threadPoolService.executeInBatches(() -> "test", 0, 5);
        assertTrue(results.isEmpty());
    }

    @Test
    public void testShutdown() {
        ThreadPoolService service = new ThreadPoolService();
        service.shutdown();
        assertDoesNotThrow(service::shutdown);
    }
}