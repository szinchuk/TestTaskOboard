package com.task.config;

import com.task.client.JokeApiClient;
import com.task.model.Joke;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@TestConfiguration
@Profile("test")
public class TestConfig {

    @Bean
    @Primary
    public JokeApiClient mockJokeApiClient() {
        return new JokeApiClient(null) {
            @Override
            public Joke getRandomJoke() {
                return new Joke(1L, "programming", "Test setup", "Test punchline");
            }
        };
    }
}