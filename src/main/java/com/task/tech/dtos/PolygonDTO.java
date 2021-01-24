package com.task.tech.dtos;

import lombok.Data;

@Data
public class PolygonDTO {
    private String id;
    private String name;
    private GeoJsonDTO geo_json;
}
