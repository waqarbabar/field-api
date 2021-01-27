package com.task.tech.dtos;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class GeoJsonDTO {
    private String type;
    private PropertyDTO properties;
    private GeometryDTO geometry;
}
