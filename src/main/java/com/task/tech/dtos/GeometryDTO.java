package com.task.tech.dtos;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class GeometryDTO {
    private String type;
    private List<List<List<Double>>> coordinates;
}
