package com.task.tech.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class WeatherHistoryDTO {
    private List<WeatherDTO> weather;
}
