package com.task.service;

import com.task.client.JokeApiClient;
import com.task.model.Joke;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JokeServiceTest {

    @Mock
    private JokeApiClient jokeApiClient;

    @Mock
    private ThreadPoolService threadPoolService;

    private JokeService jokeService;

    @BeforeEach
    public void setUp() {
        jokeService = new JokeService(jokeApiClient, threadPoolService);
    }

    @Test
    public void testGetJokesWithValidCount() {
        // Подготовка
        Joke mockJoke = new Joke(1L, "programming", "Setup", "Punchline");
        List<Joke> jokes = new ArrayList<>();
        jokes.add(mockJoke);
        jokes.add(mockJoke);
        jokes.add(mockJoke);

        when(threadPoolService.executeInBatches(ArgumentMatchers.<Supplier<Joke>>any(), eq(3), eq(10)))
                .thenReturn(jokes);

        List<Joke> result = jokeService.getJokes(3);

        assertEquals(3, result.size());
        verify(threadPoolService).executeInBatches(ArgumentMatchers.<Supplier<Joke>>any(), eq(3), eq(10));
    }

    @Test
    public void testGetJokesWithZeroCount() {
        List<Joke> jokes = jokeService.getJokes(0);

        assertTrue(jokes.isEmpty());
        verify(threadPoolService, never()).executeInBatches(any(), anyInt(), anyInt());
    }

    @Test
    public void testGetJokesExceedingMaxCount() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            jokeService.getJokes(101);
        });

        assertEquals("You can get no more than 100 jokes at a time.", exception.getMessage());
        verify(threadPoolService, never()).executeInBatches(any(), anyInt(), anyInt());
    }
}