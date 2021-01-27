package com.task.tech.util;

import com.task.tech.dtos.BoundaryDTO;
import com.task.tech.dtos.FieldDTO;
import com.task.tech.dtos.GeoJsonDTO;
import com.task.tech.dtos.GeometryDTO;
import com.task.tech.dtos.PropertyDTO;
import com.task.tech.dtos.WeatherDTO;
import com.task.tech.models.Boundary;
import com.task.tech.models.Coordinate;
import com.task.tech.models.Field;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static java.util.Arrays.asList;

@Component
public class TestHelper {

    public FieldDTO getFieldDTO(UUID fieldId) {
        FieldDTO fieldDTO = new FieldDTO();
        ZonedDateTime now = ZonedDateTime.now();
        fieldDTO.setName("test field");
        fieldDTO.setId(fieldId);
        fieldDTO.setCountryCode("HU");
        fieldDTO.setCreated(now);
        fieldDTO.setUpdated(now);

        GeoJsonDTO geoJsonDTO = new GeoJsonDTO();
        geoJsonDTO.setType("Feature");
        geoJsonDTO.setProperties(new PropertyDTO());

        GeometryDTO geometryDTO = new GeometryDTO();
        geometryDTO.setType("Polygon");

        List<Double> inner = new ArrayList<>(asList(1.111, 2.222));
        List<List<Double>> outer = new ArrayList<>();
        outer.add(inner);
        List<List<List<Double>>> coordinates = new ArrayList<>();
        coordinates.add(outer);

        geometryDTO.setCoordinates(coordinates);
        geoJsonDTO.setGeometry(geometryDTO);

        BoundaryDTO boundaryDTO = new BoundaryDTO();
        boundaryDTO.setGeoJson(geoJsonDTO);
        fieldDTO.setBoundaries(boundaryDTO);
        fieldDTO.setId(fieldId);
        return fieldDTO;
    }

    public Field getField(UUID fieldId) {
        ZonedDateTime now = ZonedDateTime.now();

        Field field = new Field();
        field.setName("test field");
        field.setFieldUuid(fieldId);
        field.setCountryCode("HU");
        field.setCreated(now);
        field.setUpdated(now);

        Boundary boundary = new Boundary();
        boundary.setBoundaryUuid(UUID.randomUUID());
        boundary.setCreated(now);
        boundary.setUpdated(now);
        boundary.setGeoJsonType("Feature");
        boundary.setGeometryType("Polygon");

        List<Coordinate> coordinates = new ArrayList<>();
        coordinates.add(new Coordinate(null, 1.111, 2.222));

        boundary.setCoordinates(coordinates);
        field.setBoundaries(boundary);
        return field;
    }

    public WeatherDTO getWeatherDTO() {
        WeatherDTO weatherDTO = new WeatherDTO();
        weatherDTO.setTimeStamp(ZonedDateTime.now().toEpochSecond() + "");
        weatherDTO.setHumidity(2.2);
        weatherDTO.setTemperature(22.4);
        weatherDTO.setTemperatureMax(35.2);
        weatherDTO.setTemperatureMin(21.1);
        return weatherDTO;
    }
}
