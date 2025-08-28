package com.mujahid.weatherclient.controller;

import com.mujahid.weatherclient.dto.WeatherResponse;
import com.mujahid.weatherclient.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/weather")
@CrossOrigin(origins = "*")

public class WeatherController {

    private final WeatherService weatherService;

    @Autowired
    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/current")
    public ResponseEntity<WeatherResponse> current(@RequestParam("city") String city) {
        WeatherResponse response = weatherService.getCurrentWeather(city);
        return ResponseEntity.ok(response);
    }
}
