package com.task.tech.services;

import com.task.tech.dtos.FieldDTO;
import com.task.tech.dtos.PaginationData;

import java.util.List;
import java.util.UUID;

public interface FieldService {

    void addNewField(FieldDTO fieldDTO);

    void updateExistingField(FieldDTO fieldDTO);

    void deleteExistingField(UUID fieldId);

    FieldDTO getExistingField(UUID fieldId);

    List<FieldDTO> getAllExistingFields(PaginationData paginationData);

    String getExistingFieldPolygonId(UUID fieldId);
}
