package com.task.tech.services.impl;

import com.task.tech.dtos.WeatherDTO;
import com.task.tech.dtos.WeatherHistoryDTO;
import com.task.tech.exceptions.EntityNotFoundException;
import com.task.tech.services.FieldService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class WeatherHistoryServiceImplTest {
    @Mock
    private FieldService fieldService;
    @Mock
    private RestTemplate restTemplate;
    @InjectMocks
    private WeatherHistoryServiceImpl serviceUnderTest;

    @BeforeEach
    void beforeEach() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void test_getWeatherHistory_callsInternalServicesOnceAndReturnsResponse() {
        // given
        UUID fieldId = UUID.randomUUID();
        String polygonId = "test";
        serviceUnderTest.setDurationInDays(7);
        given(fieldService.getExistingFieldPolygonId(fieldId)).willReturn(polygonId);
        given(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(WeatherDTO[].class)))
                .willReturn(new ResponseEntity<>(new WeatherDTO[]{mock(WeatherDTO.class)}, HttpStatus.OK));

        // when
        WeatherHistoryDTO weatherHistoryDTO = serviceUnderTest.getWeatherHistory(fieldId);

        // then
        verify(fieldService, times(1)).getExistingFieldPolygonId(fieldId);
        verify(restTemplate, times(1)).exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(WeatherDTO[].class));
        assertEquals(1, weatherHistoryDTO.getWeather().size());
    }

    @Test
    void test_getWeatherHistory_whenPolygonInternallyNotExists_throwsException() {
        // given
        UUID fieldId = UUID.randomUUID();
        serviceUnderTest.setDurationInDays(7);
        given(fieldService.getExistingFieldPolygonId(fieldId)).willReturn(null);

        // when
        assertThrows(EntityNotFoundException.class, () -> serviceUnderTest.getWeatherHistory(fieldId));

        // then
        verify(fieldService, times(1)).getExistingFieldPolygonId(fieldId);
        verify(restTemplate, times(0)).exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(WeatherDTO[].class));
    }

    @Test
    void test_getWeatherHistory_whenPolygonExternallyNotExists_throwsException() {
        // given
        String polygonId = "test";
        UUID fieldId = UUID.randomUUID();
        serviceUnderTest.setDurationInDays(7);
        given(fieldService.getExistingFieldPolygonId(fieldId)).willReturn(polygonId);
        given(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(WeatherDTO[].class)))
                .willReturn(new ResponseEntity<>(new WeatherDTO[]{mock(WeatherDTO.class)}, HttpStatus.NOT_FOUND));

        // when
        assertThrows(EntityNotFoundException.class, () -> serviceUnderTest.getWeatherHistory(fieldId));

        // then
        verify(fieldService, times(1)).getExistingFieldPolygonId(fieldId);
        verify(restTemplate, times(1)).exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(WeatherDTO[].class));
    }

    @Test
    void test_getWeatherHistory_whenExternalRequestThrowsException() {
        // given
        String polygonId = "test";
        UUID fieldId = UUID.randomUUID();
        serviceUnderTest.setDurationInDays(7);
        given(fieldService.getExistingFieldPolygonId(fieldId)).willReturn(polygonId);
        given(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(WeatherDTO[].class)))
                .willThrow(new RuntimeException());

        // when
        assertThrows(ResponseStatusException.class, () -> serviceUnderTest.getWeatherHistory(fieldId));

        // then
        verify(fieldService, times(1)).getExistingFieldPolygonId(fieldId);
        verify(restTemplate, times(1)).exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(WeatherDTO[].class));
    }
}