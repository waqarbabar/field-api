package com.task.tech.services;

import com.task.tech.dtos.WeatherHistoryDTO;

import java.util.UUID;

public interface WeatherHistoryService {
    WeatherHistoryDTO getWeatherHistory(UUID fieldId);
}
