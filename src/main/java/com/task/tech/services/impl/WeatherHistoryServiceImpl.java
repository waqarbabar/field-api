package com.task.tech.services.impl;

import com.task.tech.dtos.WeatherDTO;
import com.task.tech.dtos.WeatherHistoryDTO;
import com.task.tech.exceptions.EntityNotFoundException;
import com.task.tech.services.FieldService;
import com.task.tech.services.WeatherHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.UUID;

import static java.util.Objects.nonNull;
import static org.springframework.util.StringUtils.isEmpty;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeatherHistoryServiceImpl implements WeatherHistoryService {

    private final FieldService fieldService;
    private final RestTemplate restTemplate;

    @Value("${agro.weather-api-url}")
    private String weatherHistoryUrl;
    @Value("${agro.api-key}")
    private String key;
    @Value("${agro.weather-history-duration-in-days}")
    private Integer durationInDays;

    @Override
    public WeatherHistoryDTO getWeatherHistory(UUID fieldId) {
        String polygonId = getPolygonId(fieldId);
        log.debug("sending get request to fetch weather history of last {} days for polygon id={}", durationInDays, polygonId);
        ResponseEntity<WeatherDTO[]> response = restTemplate.exchange(getUrl(polygonId), HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), WeatherDTO[].class);
        return getWeatherHistoryDTO(fieldId, response);
    }

    private String getPolygonId(UUID fieldId) {
        String polygonId = fieldService.getExistingFieldPolygonId(fieldId);
        log.debug("fetched polygon id={} for existing field with id={}", polygonId, fieldId);
        this.validate(fieldId, polygonId);
        return polygonId;
    }

    private void validate(UUID fieldId, String polygonId) {
        if (isEmpty(polygonId)) {
            throw new EntityNotFoundException("Polygon does not exist for the field id: " + fieldId);
        }
    }

    private String getUrl(String polygonId) {
        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        long start = now.minusDays(durationInDays).toEpochSecond();
        long end = now.toEpochSecond();
        /*return weatherHistoryUrl
                + "?polyid=" + polygonId
                + "&appid=" + key
                + "&start=" + start
                + "*end=" + end;*/
        return "https://extendsclass.com/mock/rest/117281b683fd042121ef237e0744dbca/test";
    }

    private WeatherHistoryDTO getWeatherHistoryDTO(UUID fieldId, ResponseEntity<WeatherDTO[]> response) {
        if (response.getStatusCode().is2xxSuccessful() && nonNull(response.getBody())) {
            log.debug("received weather history for field id={}", fieldId);
            return new WeatherHistoryDTO(Arrays.asList(response.getBody()));
        }
        throw new EntityNotFoundException("Weather History not found for the field id: " + fieldId);
    }
}
