package com.task.tech.dtos;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PolygonDTO {
    private String id;
    private String name;
    private GeoJsonDTO geo_json;
}
