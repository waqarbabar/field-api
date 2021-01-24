package com.task.tech.services;

import com.task.tech.dtos.FieldDTO;
import com.task.tech.dtos.PaginationDTO;
import com.task.tech.models.Field;

import java.util.List;
import java.util.UUID;

public interface FieldService {

    void addNewField(FieldDTO fieldDTO);

    void updateExistingField(FieldDTO fieldDTO);

    void deleteExistingField(UUID fieldId);

    FieldDTO getExistingField(UUID fieldId);

    List<FieldDTO> getAllExistingFields(PaginationDTO paginationDTO);

    String getExistingFieldPolygonId(UUID fieldId);
}
