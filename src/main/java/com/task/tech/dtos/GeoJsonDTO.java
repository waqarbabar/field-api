package com.task.tech.dtos;

import lombok.Data;

@Data
public class GeoJsonDTO {
    private String type;
    private PropertyDTO properties;
    private GeometryDTO geometry;
}
