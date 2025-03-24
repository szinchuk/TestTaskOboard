package com.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class JokeServiceIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testJokesEndpointWithDefaultParameter() {
        ResponseEntity<List> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/api/jokes?count=10",
                List.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List jokes = response.getBody();
        assertNotNull(jokes);
        assertEquals(10, jokes.size());
    }

    @Test
    public void testJokesEndpointWithCustomParameter() {
        ResponseEntity<List> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/api/jokes?count=3",
                List.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List jokes = response.getBody();
        assertNotNull(jokes);
        assertEquals(3, jokes.size());
    }

    @Test
    public void testJokesEndpointWithInvalidParameter() {
        ResponseEntity<Map> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/api/jokes?count=101",
                Map.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, String> errorBody = response.getBody();
        assertNotNull(errorBody);
        assertTrue(errorBody.containsKey("error"));
    }
}