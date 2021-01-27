package com.task.tech.services.impl;

import com.task.tech.dtos.GeoJsonDTO;
import com.task.tech.dtos.PolygonDTO;
import com.task.tech.exceptions.EntityNotFoundException;
import com.task.tech.services.PolygonService;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Setter
@Service
@RequiredArgsConstructor
public class PolygonServiceImpl implements PolygonService {

    private final RestTemplate restTemplate;

    @Value("${agro.polygon-api-url}")
    private String polygonUrl;
    @Value("${agro.api-key}")
    private String key;

    @Override
    public String createNewPolygon(GeoJsonDTO geoJsonDTO) {
        PolygonDTO polygonDTO = getPolygonDTO(geoJsonDTO);
        log.debug("sending create polygon external request");
        ResponseEntity<PolygonDTO> responseEntity = restTemplate.postForEntity(getUrl(), polygonDTO, PolygonDTO.class);
        String polygonId = getPolygonId(responseEntity);
        log.debug("Received external response for create request for polygon id={}", polygonId);
        return polygonId;
    }

    @Override
    public void updateExistingPolygon(String polygonId, GeoJsonDTO geoJsonDTO) {
        PolygonDTO polygonDTO = getPolygonDTO(geoJsonDTO);
        polygonDTO.setId(polygonId);
        log.debug("sending update polygon external request with polygon id={}", polygonId);
        ResponseEntity<PolygonDTO> responseEntity = restTemplate.exchange(getUrl(polygonId), HttpMethod.PUT, new HttpEntity<>(polygonDTO, new HttpHeaders()), PolygonDTO.class);
        this.validateResponse(responseEntity);
        log.debug("Received external response of update request for polygon id={}", polygonId);
    }

    @Override
    public void deleteExistingPolygon(String polygonId) {
        log.debug("sending delete polygon request with polygon id={}", polygonId);
        ResponseEntity<Void> response = restTemplate.exchange(getUrl(polygonId),
                HttpMethod.DELETE, null, Void.class);
        this.validateResponse(response);
        log.debug("Received external response of delete request for polygon id={}", polygonId);
    }

    private String getPolygonId(ResponseEntity<PolygonDTO> responseEntity) {
        this.validateResponse(responseEntity);
        return responseEntity.getBody().getId();
    }

    private void validateResponse(ResponseEntity<?> responseEntity) {
        if (responseEntity.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
            throw new EntityNotFoundException("Polygon not found");
        } else if (!responseEntity.getStatusCode().is2xxSuccessful()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Exception encountered during external interaction");
        }
    }

    private PolygonDTO getPolygonDTO(GeoJsonDTO geoJsonDTO) {
        PolygonDTO polygonDTO = new PolygonDTO();
        polygonDTO.setName("test polygon");
        polygonDTO.setGeo_json(geoJsonDTO);
        return polygonDTO;
    }

    private String getUrl(String... pathVariable) {
        StringBuilder pathVariableString = new StringBuilder();
        for (int i = 0; i < pathVariable.length; i++) {
            pathVariableString.append("/").append(pathVariable[i]);
        }
        return polygonUrl + pathVariableString + "?appid=" + key;
    }
}
