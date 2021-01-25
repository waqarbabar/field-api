package com.task.tech.services.impl;

import com.task.tech.dtos.FieldDTO;
import com.task.tech.dtos.GeoJsonDTO;
import com.task.tech.dtos.PaginationData;
import com.task.tech.exceptions.EntityAlreadyException;
import com.task.tech.exceptions.EntityNotFoundException;
import com.task.tech.mappers.FieldMapper;
import com.task.tech.models.Field;
import com.task.tech.repositories.FieldRepository;
import com.task.tech.services.FieldService;
import com.task.tech.services.PolygonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import static java.util.Objects.isNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class FieldServiceImpl implements FieldService {

    private final FieldMapper mapper;
    private final FieldRepository fieldRepository;
    private final PolygonService polygonService;

    @Override
    public void addNewField(FieldDTO fieldDTO) {
        log.debug("creating new polygon for field id={}", fieldDTO.getId());
        String polygonId = createPolygon(fieldDTO);
        log.debug("new polygon with id={} created for field id={}", polygonId, fieldDTO.getId());
        Field field = getFieldToCreate(fieldDTO, polygonId);
        fieldRepository.findByFieldUuid(field.getFieldUuid()).ifPresent(f -> {
            throw new EntityAlreadyException("Field Already Exists with id: " + f.getFieldUuid());
        });
        log.debug("saving new field with field id={}", field.getFieldUuid());
        fieldRepository.save(field);
    }

    @Override
    public void updateExistingField(FieldDTO fieldDTO) {
        Field oldField = getField(fieldDTO.getId());
        log.debug("existing field found, for updation, with id={}", fieldDTO.getId());
        String polygonId = oldField.getBoundaries().getPolygonId();
        log.debug("updating existing polygon with id={} for field with id={}", polygonId, fieldDTO.getId());
        updateExistingPolygon(polygonId, fieldDTO.getBoundaries().getGeoJson());
        log.debug("existing polygon with id={} updated for field with id={}", polygonId, fieldDTO.getId());
        Field newField = getFieldToUpdate(fieldDTO, oldField);
        log.debug("updating existing field with field id={}", newField.getFieldUuid());
        fieldRepository.save(newField);
    }

    @Override
    public void deleteExistingField(UUID fieldId) {
        Field field = getField(fieldId);
        log.debug("existing field found, for deletion, with id={}", field.getFieldUuid());
        String polygonId = field.getBoundaries().getPolygonId();
        log.debug("deleting existing polygon with id={} for field with id={}", polygonId, field.getFieldUuid());
        deleteExistingPolygon(field.getBoundaries().getPolygonId());
        log.debug("deleting existing field with field id={}", field.getFieldUuid());
        fieldRepository.delete(field);
    }

    @Override
    public FieldDTO getExistingField(UUID fieldId) {
        Field field = getField(fieldId);
        log.debug("existing field found, to show, with id={}", field.getFieldUuid());
        return mapper.mapToDTO(field);
    }

    @Override
    public List<FieldDTO> getAllExistingFields(PaginationData paginationData) {
        Pageable pageable = PageRequest.of(paginationData.getPageNumber(), paginationData.getPageSize());
        log.debug("getting existing fields with page number={} and size={}", paginationData.getPageNumber(), paginationData.getPageSize());
        Page<Field> fieldPage = fieldRepository.findAll(pageable);
        paginationData.setTotalEntries(fieldPage.getTotalElements());
        paginationData.setTotalPages(fieldPage.getTotalPages());
        log.debug("found fields with total size={}", fieldPage.getTotalElements());
        return mapper.mapToDTOs(fieldPage.getContent());
    }

    @Override
    public String getExistingFieldPolygonId(UUID fieldId) {
        return getField(fieldId).getBoundaries().getPolygonId();
    }

    private Field getField(UUID fieldId) {
        return fieldRepository.findByFieldUuid(fieldId).orElseThrow(() -> new EntityNotFoundException("Field does not Exist with id: " + fieldId));
    }

    private Field getFieldToCreate(FieldDTO fieldDTO, String polygonId) {
        Field field = mapper.mapToModel(fieldDTO);
        field.getBoundaries().setPolygonId(polygonId);
        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        if (isNull(field.getCreated())) {
            field.setCreated(now);
        }
        if (isNull(field.getBoundaries().getCreated())) {
            field.getBoundaries().setCreated(now);
        }
        return field;
    }

    private Field getFieldToUpdate(FieldDTO fieldDTO, Field existingField) {
        Field newField = mapper.mapToModel(fieldDTO);
        mapOldFieldIdsToNewField(existingField, newField);
        if (isNull(newField.getCreated()))
            log.debug("field creation datetime not provided so setting to as its existing state");
            newField.setCreated(existingField.getCreated());
        if (isNull(newField.getBoundaries().getCreated()))
            log.debug("field boundary creation datetime not provided so setting to as its existing state");
            newField.setCreated(existingField.getCreated());
        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        if (isNull(newField.getUpdated())) {
            log.debug("field updated datetime not provided so setting to current time");
            newField.setUpdated(now);
        }
        if (isNull(newField.getBoundaries().getUpdated())) {
            log.debug("field boundary updated datetime not provided so setting to current time");
            newField.getBoundaries().setUpdated(now);
        }
        return newField;
    }

    private void mapOldFieldIdsToNewField(Field existingField, Field newField) {
        newField.setId(existingField.getId());
        newField.getBoundaries().setPolygonId(existingField.getBoundaries().getPolygonId());
        newField.getBoundaries().setId(existingField.getBoundaries().getId());
        for (int i = 0; i < existingField.getBoundaries().getCoordinates().size(); i++) {
            if (newField.getBoundaries().getCoordinates().size() > i + 1) {
                newField.getBoundaries().getCoordinates().get(0).setId(existingField.getBoundaries().getCoordinates().get(i).getId());
            }
        }
    }

    private String createPolygon(FieldDTO fieldDTO) {
        return polygonService.createNewPolygon(fieldDTO.getBoundaries().getGeoJson());
    }

    private void updateExistingPolygon(String polygonId, GeoJsonDTO geoJson) {
        polygonService.updateExistingPolygon(polygonId, geoJson);
    }

    private void deleteExistingPolygon(String polygonId) {
        polygonService.deleteExistingPolygon(polygonId);
    }
}
