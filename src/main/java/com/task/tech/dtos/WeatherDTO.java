package com.task.tech.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherDTO {
    private Long timestamp;
    private Double temperature;
    private Double humidity;
    private Double temperatureMax;
    private Double temperatureMin;

    @JsonProperty("dt")
    public void setTimeStamp(String dt) {
        this.timestamp = (Long.parseLong(dt));
    }

    @JsonProperty("main")
    public void setMainDetails(Map<String, String> main) {
        this.temperature = (Double.parseDouble(main.get("temp")));
        this.humidity = (Double.parseDouble(main.get("humidity")));
        this.temperatureMax = (Double.parseDouble(main.get("temp_max")));
        this.temperatureMin = (Double.parseDouble(main.get("temp_min")));
    }
}
