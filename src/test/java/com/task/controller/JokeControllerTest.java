package com.task.controller;

import com.task.model.Joke;
import com.task.service.JokeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(JokeController.class)
public class JokeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JokeService jokeService;

    @Test
    public void testGetJokesWithDefaultCount() throws Exception {
        // Подготовка
        Joke joke1 = new Joke(1L, "general", "Setup 1", "Punchline 1");
        Joke joke2 = new Joke(2L, "programming", "Setup 2", "Punchline 2");
        List<Joke> jokes = Arrays.asList(joke1, joke2);

        // Мокируем поведение jokeService для любого количества запрашиваемых шуток
        when(jokeService.getJokes(anyInt())).thenReturn(jokes);

        // Выполняем запрос и проверяем результаты
        mockMvc.perform(get("/api/jokes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].type").value("general"))
                .andExpect(jsonPath("$[0].setup").value("Setup 1"))
                .andExpect(jsonPath("$[0].punchline").value("Punchline 1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].type").value("programming"))
                .andExpect(jsonPath("$[1].setup").value("Setup 2"))
                .andExpect(jsonPath("$[1].punchline").value("Punchline 2"));
    }

    @Test
    public void testGetJokesWithSpecifiedCount() throws Exception {
        // Подготовка
        Joke joke1 = new Joke(1L, "general", "Setup 1", "Punchline 1");
        Joke joke2 = new Joke(2L, "programming", "Setup 2", "Punchline 2");
        Joke joke3 = new Joke(3L, "dad", "Setup 3", "Punchline 3");
        List<Joke> jokes = Arrays.asList(joke1, joke2, joke3);

        // Мокируем поведение jokeService для конкретного количества шуток = 3
        when(jokeService.getJokes(3)).thenReturn(jokes);

        // Выполняем запрос с указанным count и проверяем результаты
        mockMvc.perform(get("/api/jokes?count=3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[2].id").value(3));
    }
}