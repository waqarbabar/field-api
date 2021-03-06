package com.task.tech.services;

import com.task.tech.dtos.GeoJsonDTO;

public interface PolygonService {

    String createNewPolygon(GeoJsonDTO polygonDTO);

    void updateExistingPolygon(String polygonId, GeoJsonDTO geoJsonDTO);

    void deleteExistingPolygon(String polygonId);
}
