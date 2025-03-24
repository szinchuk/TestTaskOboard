package com.task.client;

import com.task.model.Joke;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
@Slf4j
public class JokeApiClient {

    private static final String JOKE_API_URL = "https://official-joke-api.appspot.com/random_joke";

    private final RestTemplate restTemplate;

    public Joke getRandomJoke() {
        try {
            log.debug("Fetching joke from {}", JOKE_API_URL);
            Joke joke = restTemplate.getForObject(JOKE_API_URL, Joke.class);

            if (joke == null) {
                log.warn("API returned null joke");
                throw new RuntimeException("Внешний API вернул пустой ответ");
            }

            log.debug("Successfully fetched joke with id: {}", joke.getId());
            return joke;
        } catch (HttpStatusCodeException e) {
            log.error("HTTP error when fetching joke. Status: {}, Response: {}",
                    e.getStatusCode(), e.getResponseBodyAsString(), e);

            if (e.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                throw new RuntimeException("Превышен лимит запросов к внешнему API", e);
            }

            throw new RuntimeException("Ошибка при получении шутки из внешнего API: " + e.getStatusCode(), e);
        } catch (ResourceAccessException e) {
            log.error("Network error when fetching joke", e);
            throw new RuntimeException("Проблема с сетевым подключением к внешнему API", e);
        } catch (RestClientException e) {
            log.error("Error fetching joke from external API", e);
            throw new RuntimeException("Ошибка при получении шутки из внешнего сервиса", e);
        }
    }
}