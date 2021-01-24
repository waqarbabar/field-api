package com.task.tech.mappers;

import com.task.tech.dtos.FieldDTO;
import com.task.tech.dtos.PropertyDTO;
import com.task.tech.models.Coordinate;
import com.task.tech.models.Field;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class FieldMapper {

    private final ModelMapper modelMapper;

    @Autowired
    public FieldMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
        configureDTOtoModelMapping(modelMapper);
        configureModelToDTOMapping(modelMapper);
    }

    public Field mapToModel(FieldDTO fieldDTO) {
        return modelMapper.map(fieldDTO, Field.class);
    }

    public FieldDTO mapToDTO(Field field) {
        return modelMapper.map(field, FieldDTO.class);
    }

    public List<FieldDTO> mapToDTOs(List<Field> allFields) {
        return allFields
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private void configureDTOtoModelMapping(ModelMapper modelMapper) {
        Converter<List<List<List<Double>>>, List<Coordinate>> coordinateConverter = ctx -> ctx.getSource()
                .stream()
                .flatMap(Collection::stream)
                .map(list -> {
                    Coordinate coordinate = new Coordinate();
                    coordinate.setLatitude(list.get(0));
                    coordinate.setLongitude(list.get(1));
                    return coordinate;
                }).collect(Collectors.toList());
        modelMapper.typeMap(FieldDTO.class, Field.class)
                .addMapping(FieldDTO::getId, Field::setFieldUuid)
                .addMapping(src -> src.getBoundaries().getId(), (Field dest, UUID v) -> dest.getBoundaries().setBoundaryUuid(v))
                .addMapping(src -> src.getBoundaries().getGeoJson().getType(), (Field dest, String v) -> dest.getBoundaries().setGeoJsonType(v))
                .addMapping(src -> src.getBoundaries().getGeoJson().getGeometry().getType(), (Field dest, String v) -> dest.getBoundaries().setGeometryType(v))
                .addMappings(mp -> {
                    mp.skip(Field::setId);
                    mp.skip((Field dest, Long v) -> dest.getBoundaries().setId(v));
                    mp.skip((Field dest, String v) -> dest.getBoundaries().setPolygonId(v));
                    mp.using(coordinateConverter)
                            .map(src -> src.getBoundaries().getGeoJson().getGeometry().getCoordinates(), (Field dest, List<Coordinate> v) -> dest.getBoundaries().setCoordinates(v));
                });
    }

    private void configureModelToDTOMapping(ModelMapper modelMapper) {
        Converter<List<Coordinate>, List<List<List<Double>>>> coordinateConverter = ctx -> {
            List<List<Double>> result = ctx.getSource()
                    .stream()
                    .map(coordinate -> {
                        List<Double> output = new ArrayList<>();
                        output.add(coordinate.getLatitude());
                        output.add(coordinate.getLongitude());
                        return output;
                    }).collect(Collectors.toList());
            List<List<List<Double>>> output = new ArrayList<>();
            output.add(result);
            return output;
        };
        modelMapper.typeMap(Field.class, FieldDTO.class)
                .addMapping(Field::getFieldUuid, FieldDTO::setId)
                .addMapping(src -> src.getBoundaries().getBoundaryUuid(), (FieldDTO dest, UUID v) -> dest.getBoundaries().setId(v))
                .addMapping(src -> src.getBoundaries().getGeoJsonType(), (FieldDTO dest, String v) -> dest.getBoundaries().getGeoJson().setType(v))
                .addMapping(src -> src.getBoundaries().getGeometryType(), (FieldDTO dest, String v) -> dest.getBoundaries().getGeoJson().getGeometry().setType(v))
                .addMappings(mp -> {
                    mp.skip((FieldDTO dest, PropertyDTO v) -> dest.getBoundaries().getGeoJson().setProperties(v));
                    mp.using(coordinateConverter)
                            .map(src -> src.getBoundaries().getCoordinates(), (FieldDTO dest, List<List<List<Double>>> v) -> dest.getBoundaries().getGeoJson().getGeometry().setCoordinates(v));
                });
    }
}
