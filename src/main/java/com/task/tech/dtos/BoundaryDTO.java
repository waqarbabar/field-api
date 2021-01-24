package com.task.tech.dtos;

import lombok.Data;

import java.time.ZonedDateTime;
import java.util.UUID;

@Data
public class BoundaryDTO {
    private UUID id;
    private ZonedDateTime created;
    private ZonedDateTime updated;
    private GeoJsonDTO geoJson;
}
