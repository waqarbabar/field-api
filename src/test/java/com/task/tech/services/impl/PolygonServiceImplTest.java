package com.task.tech.services.impl;

import com.task.tech.dtos.GeoJsonDTO;
import com.task.tech.dtos.PolygonDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class PolygonServiceImplTest {

    @Captor
    ArgumentCaptor<String> urlCaptor;
    @Mock
    private RestTemplate restTemplate;
    @InjectMocks
    private PolygonServiceImpl serviceUnderTest;

    @BeforeEach
    void beforeEach() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void test_createNewPolygon_callsInternalServiceOnce() {
        // given
        PolygonDTO polygonDTO = new PolygonDTO();
        polygonDTO.setId("test");
        given(restTemplate.postForEntity(anyString(), any(PolygonDTO.class), any())).willReturn(new ResponseEntity<>(polygonDTO, HttpStatus.CREATED));

        // when
        serviceUnderTest.createNewPolygon(mock(GeoJsonDTO.class));

        // then
        verify(restTemplate, times(1)).postForEntity(anyString(), any(PolygonDTO.class), any());
    }

    @Test
    void test_createNewPolygon_withValidURL() {
        // given
        PolygonDTO polygonDTO = new PolygonDTO();
        polygonDTO.setId("test");
        String key = "test-key";
        String polygonUrl = "test-url";
        serviceUnderTest.setKey(key);
        serviceUnderTest.setPolygonUrl(polygonUrl);
        given(restTemplate.postForEntity(anyString(), any(PolygonDTO.class), any())).willReturn(new ResponseEntity<>(polygonDTO, HttpStatus.CREATED));

        // when
        serviceUnderTest.createNewPolygon(mock(GeoJsonDTO.class));

        // then
        verify(restTemplate, times(1)).postForEntity(urlCaptor.capture(), any(PolygonDTO.class), any());
        assertEquals(polygonUrl + "?appid=" + key, urlCaptor.getValue());
    }

    @Test
    void test_createNewPolygon_whenExternalIssueOccurs_ThrowsException() {
        // given
        PolygonDTO polygonDTO = new PolygonDTO();
        polygonDTO.setId("test");
        given(restTemplate.postForEntity(anyString(), any(PolygonDTO.class), any())).willReturn(new ResponseEntity<>(polygonDTO, HttpStatus.BAD_REQUEST));

        // when
        assertThrows(ResponseStatusException.class, () -> serviceUnderTest.createNewPolygon(mock(GeoJsonDTO.class)));

        // then
        verify(restTemplate, times(1)).postForEntity(anyString(), any(PolygonDTO.class), any());
    }

    @Test
    void test_updateExistingPolygon_callsInternalServiceOnce() {
        // given
        String polygonId = "test";
        PolygonDTO polygonDTO = new PolygonDTO();
        polygonDTO.setId(polygonId);
        given(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(PolygonDTO.class))).willReturn(new ResponseEntity<>(polygonDTO, HttpStatus.OK));

        // when
        serviceUnderTest.updateExistingPolygon(polygonId, mock(GeoJsonDTO.class));

        // then
        verify(restTemplate, times(1)).exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(PolygonDTO.class));
    }

    @Test
    void test_updateExistingPolygon_whenExternalIssueOccurs_ThrowsException() {
        // given
        String polygonId = "test";
        PolygonDTO polygonDTO = new PolygonDTO();
        polygonDTO.setId(polygonId);
        given(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(PolygonDTO.class))).willReturn(new ResponseEntity<>(polygonDTO, HttpStatus.BAD_REQUEST));

        // when
        assertThrows(ResponseStatusException.class, () -> serviceUnderTest.updateExistingPolygon(polygonId, mock(GeoJsonDTO.class)));
    }

    @Test
    void test_updateExistingPolygon_withValidURL() {
        // given
        String polygonId = "test";
        String key = "test-key";
        String polygonUrl = "test-url";
        PolygonDTO polygonDTO = new PolygonDTO();
        polygonDTO.setId(polygonId);
        serviceUnderTest.setKey(key);
        serviceUnderTest.setPolygonUrl(polygonUrl);
        given(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(PolygonDTO.class))).willReturn(new ResponseEntity<>(polygonDTO, HttpStatus.OK));

        // when
        serviceUnderTest.updateExistingPolygon(polygonId, mock(GeoJsonDTO.class));

        // then
        verify(restTemplate, times(1)).exchange(urlCaptor.capture(), any(HttpMethod.class), any(HttpEntity.class), eq(PolygonDTO.class));
        assertEquals(polygonUrl + "/" + polygonId + "?appid=" + key, urlCaptor.getValue());
    }


    @Test
    void test_deleteExistingPolygon_callsInternalServiceOnce() {
        // given
        String polygonId = "test";
        doReturn(new ResponseEntity<>(null, HttpStatus.OK)).when(restTemplate)
                .exchange(anyString(), any(HttpMethod.class), any(), eq(Void.class));

        // when
        serviceUnderTest.deleteExistingPolygon(polygonId);

        // then
        verify(restTemplate, times(1)).exchange(anyString(), any(HttpMethod.class), any(), eq(Void.class));
    }

    @Test
    void test_deleteExistingPolygon_withValidURL() {
        // given
        String polygonId = "test";
        String key = "test-key";
        String polygonUrl = "test-url";
        serviceUnderTest.setKey(key);
        serviceUnderTest.setPolygonUrl(polygonUrl);
        doReturn(new ResponseEntity<>(null, HttpStatus.OK)).when(restTemplate)
                .exchange(anyString(), any(HttpMethod.class), any(), eq(Void.class));

        // when
        serviceUnderTest.deleteExistingPolygon(polygonId);

        // then
        verify(restTemplate, times(1)).exchange(urlCaptor.capture(), any(HttpMethod.class), any(), eq(Void.class));
        assertEquals(polygonUrl + "/" + polygonId + "?appid=" + key, urlCaptor.getValue());
    }
}