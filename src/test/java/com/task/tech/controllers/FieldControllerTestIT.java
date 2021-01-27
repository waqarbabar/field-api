package com.task.tech.controllers;

import com.task.tech.FieldApplication;
import com.task.tech.dtos.FieldDTO;
import com.task.tech.dtos.PolygonDTO;
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
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;

@ActiveProfiles("test")
@SpringBootTest(classes = {FieldApplication.class}, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class FieldControllerTestIT {
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
    void test_addNewField_savesField() {
        // given
        UUID fieldId = UUID.randomUUID();
        FieldDTO fieldDTO = helper.getFieldDTO(fieldId);
        PolygonDTO polygonDTO = new PolygonDTO();
        polygonDTO.setId("test-polygon-id");
        given(mockRestTemplate.postForEntity(anyString(), any(PolygonDTO.class), any()))
                .willReturn(new ResponseEntity<>(polygonDTO, HttpStatus.CREATED));

        // When
        ResponseEntity<String> response = restTemplate.postForEntity(createURLWithPort(""), fieldDTO, String.class);

        // Then
        assertTrue(response.getStatusCode().is2xxSuccessful());
        Optional<Field> fieldOptional = fieldRepository.findByFieldUuid(fieldId);
        assertTrue(fieldOptional.isPresent());
        reset();
    }

    @Test
    void test_addNewField_notSavedField_ReturnsConflict() {
        // given
        UUID fieldId = UUID.randomUUID();
        FieldDTO fieldDTO = helper.getFieldDTO(fieldId);
        PolygonDTO polygonDTO = new PolygonDTO();
        polygonDTO.setId("test-polygon-id");
        fieldRepository.save(helper.getField(fieldId));
        given(mockRestTemplate.postForEntity(anyString(), any(PolygonDTO.class), any()))
                .willReturn(new ResponseEntity<>(polygonDTO, HttpStatus.CREATED));

        // When
        ResponseEntity<String> response = restTemplate.postForEntity(createURLWithPort(""), fieldDTO, String.class);

        // Then
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(1, fieldRepository.findAll().size());
        reset();
    }

    @Test
    void test_updateField_updatesField() {
        // given
        String updateName = "updated-name";
        UUID fieldId = UUID.randomUUID();
        FieldDTO fieldDTO = helper.getFieldDTO(fieldId);
        fieldDTO.setName(updateName);
        PolygonDTO polygonDTO = new PolygonDTO();
        polygonDTO.setId("test-polygon-id");
        fieldRepository.save(helper.getField(fieldId));
        doReturn(new ResponseEntity<>(polygonDTO, HttpStatus.OK)).when(mockRestTemplate)
                .exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(PolygonDTO.class));

        // When
        ResponseEntity<String> response = restTemplate.exchange(createURLWithPort(""),
                HttpMethod.PUT, new HttpEntity<>(fieldDTO, new HttpHeaders()), String.class);

        // Then
        assertTrue(response.getStatusCode().is2xxSuccessful());
        Optional<Field> fieldOptional = fieldRepository.findByFieldUuid(fieldId);
        assertTrue(fieldOptional.isPresent());
        assertEquals(fieldDTO.getName(), fieldOptional.get().getName());
        reset();
    }

    @Test
    void test_updateField_notUpdatesField_ReturnsNotFound() {
        // given
        String updateName = "updated-name";
        UUID fieldId = UUID.randomUUID();
        FieldDTO fieldDTO = helper.getFieldDTO(fieldId);
        fieldDTO.setName(updateName);
        PolygonDTO polygonDTO = new PolygonDTO();
        polygonDTO.setId("test-polygon-id");
        doReturn(new ResponseEntity<>(polygonDTO, HttpStatus.OK)).when(mockRestTemplate)
                .exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(PolygonDTO.class));

        // When
        ResponseEntity<String> response = restTemplate.exchange(createURLWithPort(""),
                HttpMethod.PUT, new HttpEntity<>(fieldDTO, new HttpHeaders()), String.class);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(fieldRepository.findByFieldUuid(fieldId).isEmpty());
    }

    @Test
    void test_deleteField_deletesField() {
        // given
        String updateName = "updated-name";
        UUID fieldId = UUID.randomUUID();
        FieldDTO fieldDTO = helper.getFieldDTO(fieldId);
        fieldDTO.setName(updateName);
        PolygonDTO polygonDTO = new PolygonDTO();
        polygonDTO.setId("test-polygon-id");
        fieldRepository.save(helper.getField(fieldId));
        doReturn(new ResponseEntity<>(null, HttpStatus.OK)).when(mockRestTemplate)
                .exchange(anyString(), any(HttpMethod.class), any(), eq(Void.class));

        // When
        ResponseEntity<Void> response = restTemplate.exchange(createURLWithPort("/" + fieldId),
                HttpMethod.DELETE, null, Void.class);

        // Then
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertTrue(fieldRepository.findByFieldUuid(fieldId).isEmpty());
        reset();
    }

    @Test
    void test_deleteField_notDeletesField_ReturnsNotFound() {
        // given
        String updateName = "updated-name";
        UUID fieldId = UUID.randomUUID();
        FieldDTO fieldDTO = helper.getFieldDTO(fieldId);
        fieldDTO.setName(updateName);
        PolygonDTO polygonDTO = new PolygonDTO();
        polygonDTO.setId("test-polygon-id");
        doNothing().when(mockRestTemplate)
                .delete(anyString());

        // When
        ResponseEntity<Void> response = restTemplate.exchange(createURLWithPort("/" + fieldId),
                HttpMethod.DELETE, null, Void.class);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(fieldRepository.findByFieldUuid(fieldId).isEmpty());
    }

    @Test
    void test_getField_returnsField() {
        // given
        UUID fieldId = UUID.randomUUID();
        fieldRepository.save(helper.getField(fieldId));

        // When
        ResponseEntity<FieldDTO> response = restTemplate.exchange(createURLWithPort("/" + fieldId),
                HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), FieldDTO.class);

        // Then
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertEquals(fieldId, response.getBody().getId());
        reset();
    }

    @Test
    void test_getField_ReturnsNotFound() {
        // given
        UUID fieldId = UUID.randomUUID();

        // When
        ResponseEntity<FieldDTO> response = restTemplate.exchange(createURLWithPort("/" + fieldId),
                HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), FieldDTO.class);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(fieldRepository.findByFieldUuid(fieldId).isEmpty());
    }

    @Test
    void test_getAllFields_returnsFieldsWithCorrectHeader() {
        // given
        int pageNumber = 0;
        int pageSize = 3;
        int totalElements = 5;
        int totalPages = (int) Math.ceil((double) totalElements / pageSize);
        IntStream.range(0, totalElements).forEach(i -> fieldRepository.save(helper.getField(UUID.randomUUID())));

        // When
        ResponseEntity<List<FieldDTO>> response = restTemplate.exchange(createURLWithPort("?pageNumber=" + pageNumber + "&pageSize=" + pageSize),
                HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), new ParameterizedTypeReference<>() {
                });

        // Then
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertEquals(pageSize, response.getBody().size());
        assertEquals(totalElements, Integer.parseInt(response.getHeaders().get("total_entries").get(0)));
        assertEquals(totalPages, Integer.parseInt(response.getHeaders().get("total_pages").get(0)));
        reset();
    }

    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + "/api/v1/fields" + uri;
    }

    private void reset() {
        fieldRepository.deleteAll();
    }
}