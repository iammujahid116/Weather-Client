package com.mujahid.weatherclient.service;

import com.mujahid.weatherclient.dto.WeatherResponse;

public interface WeatherService {
    WeatherResponse getCurrentWeather(String city);
}
