package com.task.tech.dtos;

import lombok.Data;

import java.util.List;

@Data
public class GeometryDTO {
    private String type;
    private List<List<List<Double>>> coordinates;
}
