package com.mujahid.weatherclient.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Component
public class OpenWeatherClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String apiKey;
    private final String baseUrl;

    public OpenWeatherClient(RestTemplate restTemplate,
                             ObjectMapper objectMapper,
                             @Value("${openweathermap.api.key}") String apiKey,
                             @Value("${openweathermap.base-url:https://api.openweathermap.org/data/2.5}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
    }
    public JsonNode getCurrentWeather(String city) {
        URI uri = UriComponentsBuilder.fromHttpUrl(baseUrl + "/weather")
                .queryParam("q", city)
                .queryParam("units", "metric")
                .queryParam("appid", apiKey)
                .build(true)
                .toUri();

        return restTemplate.getForObject(uri, JsonNode.class);
    }
}
