package com.mujahid.weatherclient.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.mujahid.weatherclient.client.OpenWeatherClient;
import com.mujahid.weatherclient.dto.WeatherResponse;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;

import java.time.Instant;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;


@Service
public class WeatherServiceImpl implements WeatherService {

    private final OpenWeatherClient openWeatherClient;
    private final ConcurrentHashMap<String, CacheEntry> cache = new ConcurrentHashMap<>();
    private final long ttlMillis = 10 * 60 * 1000; // 10 minutes

    public WeatherServiceImpl(OpenWeatherClient openWeatherClient) {
        this.openWeatherClient = openWeatherClient;
    }

    @Override
    public WeatherResponse getCurrentWeather(String city) {
        if (!StringUtils.hasText(city)) {
            throw new IllegalArgumentException("city must be provided");
        }

        String key = city.trim().toLowerCase(Locale.ROOT);
        CacheEntry entry = cache.get(key);
        if (entry != null && !entry.isExpired()) {
            return entry.response;
        }

        try {
            JsonNode raw = openWeatherClient.getCurrentWeather(city);
            WeatherResponse response = mapToWeatherResponse(raw);
            cache.put(key, new CacheEntry(response, System.currentTimeMillis() + ttlMillis));
            return response;
        } catch (HttpClientErrorException ex) {
            // rethrow to be handled by ControllerAdvice
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException("Failed to fetch weather: " + ex.getMessage(), ex);
        }
    }

    private WeatherResponse mapToWeatherResponse(JsonNode node) {
        String city = node.path("name").asText(null);
        long timestamp = node.path("dt").asLong(Instant.now().getEpochSecond());
        double temp = node.path("main").path("temp").asDouble(Double.NaN);
        int humidity = node.path("main").path("humidity").asInt(-1);
        double wind = node.path("wind").path("speed").asDouble(Double.NaN);
        String description = "";
        if (node.has("weather") && node.path("weather").isArray() && node.path("weather").size() > 0) {
            description = node.path("weather").get(0).path("description").asText("");
        }

        return new WeatherResponse(city, temp, description, humidity, wind, timestamp);
    }

    private static class CacheEntry {
        final WeatherResponse response;
        final long expiresAt;

        CacheEntry(WeatherResponse response, long expiresAt) {
            this.response = response;
            this.expiresAt = expiresAt;
        }

        boolean isExpired() {
            return System.currentTimeMillis() > expiresAt;
        }
    }
}
