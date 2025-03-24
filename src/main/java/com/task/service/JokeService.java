package com.task.service;

import com.task.client.JokeApiClient;
import com.task.model.Joke;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class JokeService {

    private static final int BATCH_SIZE = 10; // r
    private static final int MAX_JOKES = 100;

    private final JokeApiClient jokeApiClient;
    private final ThreadPoolService threadPoolService;

    public List<Joke> getJokes(int count) {
        if (count > MAX_JOKES) {
            throw new IllegalArgumentException("You can get no more than 100 jokes at a time.");
        }

        if (count <= 0) {
            return new ArrayList<>();
        }

        log.info("Fetching {} jokes in batches of {}", count, BATCH_SIZE);
        return threadPoolService.executeInBatches(jokeApiClient::getRandomJoke, count, BATCH_SIZE);
    }
}