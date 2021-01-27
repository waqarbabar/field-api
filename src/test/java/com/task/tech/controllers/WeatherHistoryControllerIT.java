package com.task.tech.controllers;

import com.task.tech.FieldApplication;
import com.task.tech.dtos.WeatherDTO;
import com.task.tech.dtos.WeatherHistoryDTO;
import com.task.tech.models.Field;
import com.task.tech.repositories.FieldRepository;
import com.task.tech.util.TestHelper;
import lombok.Setter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;

@ActiveProfiles("test")
@SpringBootTest(classes = {FieldApplication.class}, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class WeatherHistoryControllerIT {
    @LocalServerPort
    @Setter
    private int port;
    @MockBean
    private RestTemplate mockRestTemplate;
    @Autowired
    private FieldRepository fieldRepository;
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private TestHelper helper;

    @Test
    void test_getFieldWeatherHistory_whenFieldExists_getsWeatherHistoryWithResponseOk() {
        // given
        UUID fieldId = UUID.randomUUID();
        String polygonId = "test-polygon-id";
        Field field = helper.getField(fieldId);
        field.getBoundaries().setPolygonId(polygonId);
        fieldRepository.save(field);
        doReturn(new ResponseEntity<>(new WeatherDTO[]{helper.getWeatherDTO()}, HttpStatus.OK)).when(mockRestTemplate)
                .exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(WeatherDTO[].class));

        // When
        ResponseEntity<WeatherHistoryDTO> response = restTemplate.exchange(createURLWithPort("/" + fieldId + "/weather"),
                HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), WeatherHistoryDTO.class);

        // Then
        assertTrue(response.getStatusCode().is2xxSuccessful());
        Optional<Field> fieldOptional = fieldRepository.findByFieldUuid(fieldId);
        assertTrue(fieldOptional.isPresent());
        reset();
    }

    @Test
    void test_getFieldWeatherHistory_whenFieldExistsButPolygonIdDoesNotMatch_getsWeatherHistoryWithResponseOk() {
        // given
        UUID fieldId = UUID.randomUUID();
        fieldRepository.save(helper.getField(fieldId));

        // When
        ResponseEntity<WeatherHistoryDTO> response = restTemplate.exchange(createURLWithPort("/" + fieldId + "/weather"),
                HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), WeatherHistoryDTO.class);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(fieldRepository.findByFieldUuid(fieldId).get().getBoundaries().getPolygonId());
        reset();
    }

    @Test
    void test_getFieldWeatherHistory_whenFieldNotExists_returnsNotFound() {
        // given
        UUID fieldId = UUID.randomUUID();

        // When
        ResponseEntity<WeatherHistoryDTO> response = restTemplate.exchange(createURLWithPort("/" + fieldId + "/weather"),
                HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), WeatherHistoryDTO.class);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(fieldRepository.findByFieldUuid(fieldId).isEmpty());
    }


    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + "/api/v1/fields" + uri;
    }

    private void reset() {
        fieldRepository.deleteAll();
    }
}