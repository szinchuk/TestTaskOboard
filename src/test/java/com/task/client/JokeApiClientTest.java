package com.task.client;

import com.task.model.Joke;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JokeApiClientTest {

    private static final String JOKE_API_URL = "https://official-joke-api.appspot.com/random_joke";
    @Mock
    private RestTemplate restTemplate;
    private JokeApiClient jokeApiClient;

    @BeforeEach
    public void setUp() {
        jokeApiClient = new JokeApiClient(restTemplate);
    }

    @Test
    public void testGetRandomJokeSuccess() {
        // Настройка мока
        Joke expectedJoke = new Joke(1L, "programming", "Setup", "Punchline");
        when(restTemplate.getForObject(eq(JOKE_API_URL), eq(Joke.class)))
                .thenReturn(expectedJoke);

        // Выполнение
        Joke actualJoke = jokeApiClient.getRandomJoke();

        // Проверка результатов
        assertEquals(expectedJoke, actualJoke);
    }

    @Test
    public void testGetRandomJokeFailure() {
        // Настройка мока для выброса исключения
        when(restTemplate.getForObject(anyString(), eq(Joke.class)))
                .thenThrow(new RestClientException("API недоступен"));

        // Проверка выброса исключения
        Exception exception = assertThrows(RuntimeException.class, () -> {
            jokeApiClient.getRandomJoke();
        });

        assertTrue(exception.getMessage().contains("Ошибка при получении шутки из внешнего сервиса"));
    }
}