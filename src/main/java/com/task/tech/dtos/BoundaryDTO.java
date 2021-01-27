package com.task.tech.dtos;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.ZonedDateTime;
import java.util.UUID;

@Getter
@Setter
@ToString
public class BoundaryDTO {
    private UUID id;
    private ZonedDateTime created;
    private ZonedDateTime updated;
    private GeoJsonDTO geoJson;
}
