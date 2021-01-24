package com.task.tech.dtos;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import java.util.UUID;

@Data
public class FieldDTO {
    @NotNull(message = "id can not be null")
    private UUID id;
    @NotNull(message = "name can not be null")
    private String name;
    private ZonedDateTime created;
    private ZonedDateTime updated;
    @NotNull(message = "country code can not be null")
    private String countryCode;
    private BoundaryDTO boundaries;
}
