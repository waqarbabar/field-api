package com.task.tech.services.impl;


import com.task.tech.dtos.BoundaryDTO;
import com.task.tech.dtos.FieldDTO;
import com.task.tech.dtos.GeoJsonDTO;
import com.task.tech.dtos.PaginationData;
import com.task.tech.exceptions.EntityAlreadyException;
import com.task.tech.exceptions.EntityNotFoundException;
import com.task.tech.mappers.FieldMapper;
import com.task.tech.models.Boundary;
import com.task.tech.models.Coordinate;
import com.task.tech.models.Field;
import com.task.tech.repositories.FieldRepository;
import com.task.tech.services.PolygonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class FieldServiceImplTest {

    @Captor
    ArgumentCaptor<Field> fieldCaptor;
    @Mock
    private FieldMapper fieldMapper;
    @Mock
    private FieldRepository fieldRepository;
    @Mock
    private PolygonService polygonService;
    @InjectMocks
    private FieldServiceImpl serviceUnderTest;

    @BeforeEach
    void beforeEach() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void test_addNewField_whenAlreadyNotExists_newFieldSaved() {
        // given
        UUID fieldUuid = UUID.randomUUID();
        given(polygonService.createNewPolygon(any(GeoJsonDTO.class))).willReturn("test_polygon");
        given(fieldMapper.mapToModel(any(FieldDTO.class))).willReturn(getField(fieldUuid));
        given(fieldRepository.findByFieldUuid(any(UUID.class))).willReturn(Optional.empty());

        // when
        serviceUnderTest.addNewField(getFieldDTO(fieldUuid));

        // then
        verify(fieldRepository, times(1)).findByFieldUuid(fieldUuid);
        verify(fieldRepository, times(1)).save(fieldCaptor.capture());
        assertEquals(fieldUuid, fieldCaptor.getValue().getFieldUuid());
    }

    @Test
    void test_addNewField_whenAlreadyExists_newFieldNotSavedAndThrowsException() {
        // given
        UUID fieldUuid = UUID.randomUUID();
        given(polygonService.createNewPolygon(any(GeoJsonDTO.class))).willReturn("test_polygon");
        given(fieldMapper.mapToModel(any(FieldDTO.class))).willReturn(getField(fieldUuid));
        given(fieldRepository.findByFieldUuid(any(UUID.class))).willReturn(Optional.of(mock(Field.class)));

        // when
        assertThrows(EntityAlreadyException.class, () -> serviceUnderTest.addNewField(getFieldDTO(fieldUuid)));

        // then
        verify(fieldRepository, times(1)).findByFieldUuid(fieldUuid);
        verify(fieldRepository, times(0)).save(any(Field.class));
    }

    @Test
    void test_updateExistingField_whenAlreadyExists_FieldUpdated() {
        // given
        UUID fieldUuid = UUID.randomUUID();
        given(fieldMapper.mapToModel(any(FieldDTO.class))).willReturn(getField(fieldUuid));
        given(fieldRepository.findByFieldUuid(any(UUID.class))).willReturn(Optional.of(getField(fieldUuid)));

        // when
        serviceUnderTest.updateExistingField(getFieldDTO(fieldUuid));

        // then
        verify(fieldRepository, times(1)).findByFieldUuid(fieldUuid);
        verify(fieldRepository, times(1)).save(fieldCaptor.capture());
        assertEquals(fieldUuid, fieldCaptor.getValue().getFieldUuid());
    }

    @Test
    void test_updateExistingField_whenAlreadyNotExists_FieldNotUpdatedAndThrowsException() {
        // given
        UUID fieldUuid = UUID.randomUUID();
        given(fieldMapper.mapToModel(any(FieldDTO.class))).willReturn(getField(fieldUuid));
        given(fieldRepository.findByFieldUuid(any(UUID.class))).willReturn(Optional.empty());

        // when
        assertThrows(EntityNotFoundException.class, () -> serviceUnderTest.updateExistingField(getFieldDTO(fieldUuid)));

        // then
        verify(fieldRepository, times(1)).findByFieldUuid(fieldUuid);
        verify(fieldRepository, times(0)).save(any(Field.class));
    }

    @Test
    void test_deleteExistingField_whenAlreadyExists_FieldDeleted() {
        // given
        UUID fieldUuid = UUID.randomUUID();
        given(fieldMapper.mapToModel(any(FieldDTO.class))).willReturn(getField(fieldUuid));
        given(fieldRepository.findByFieldUuid(any(UUID.class))).willReturn(Optional.of(getField(fieldUuid)));

        // when
        serviceUnderTest.deleteExistingField(fieldUuid);

        // then
        verify(fieldRepository, times(1)).findByFieldUuid(fieldUuid);
        verify(fieldRepository, times(1)).delete(any(Field.class));
    }

    @Test
    void test_deleteExistingField_whenAlreadyNotExists_FieldNotDeletedAndThrowsException() {
        // given
        UUID fieldUuid = UUID.randomUUID();
        given(fieldMapper.mapToModel(any(FieldDTO.class))).willReturn(getField(fieldUuid));
        given(fieldRepository.findByFieldUuid(any(UUID.class))).willReturn(Optional.empty());

        // when
        assertThrows(EntityNotFoundException.class, () -> serviceUnderTest.deleteExistingField(fieldUuid));

        // then
        verify(fieldRepository, times(1)).findByFieldUuid(fieldUuid);
        verify(fieldRepository, times(0)).delete(any(Field.class));
    }

    @Test
    void test_getExistingField_whenAlreadyExists_ReturnsFieldDTO() {
        // given
        UUID fieldUuid = UUID.randomUUID();
        given(fieldRepository.findByFieldUuid(any(UUID.class))).willReturn(Optional.of(getField(fieldUuid)));

        // when
        serviceUnderTest.getExistingField(fieldUuid);

        // then
        verify(fieldRepository, times(1)).findByFieldUuid(fieldUuid);
        verify(fieldMapper, times(1)).mapToDTO(any(Field.class));
    }

    @Test
    void test_getExistingField_whenNotExists_throwsException() {
        // given
        UUID fieldUuid = UUID.randomUUID();
        given(fieldRepository.findByFieldUuid(fieldUuid)).willReturn(Optional.empty());

        // when
        assertThrows(EntityNotFoundException.class, () -> serviceUnderTest.getExistingField(fieldUuid));

        // then
        verify(fieldRepository, times(1)).findByFieldUuid(fieldUuid);
        verify(fieldMapper, times(0)).mapToDTO(any(Field.class));
    }

    @Test
    void test_getAllExistingFields_whenFieldsExist_ReturnsResult() {
        // given
        PaginationData paginationData = new PaginationData(0, 5);
        Page<Field> page = new PageImpl<>(Collections.singletonList(getField(UUID.randomUUID())));
        given(fieldRepository.findAll(any(PageRequest.class))).willReturn(page);
        // when
        serviceUnderTest.getAllExistingFields(paginationData);

        // then
        verify(fieldRepository, times(1)).findAll(any(PageRequest.class));
        verify(fieldMapper, times(1)).mapToDTOs(any(List.class));
        assertEquals(page.getTotalElements(), paginationData.getTotalEntries());
        assertEquals(page.getTotalPages(), paginationData.getTotalPages());
    }

    @Test
    void test_getAllExistingFields_whenFieldsNotExist_ReturnsResult() {
        // given
        PaginationData paginationData = new PaginationData(0, 5);
        Page<Field> page = new PageImpl<>(Collections.emptyList());
        given(fieldRepository.findAll(any(PageRequest.class))).willReturn(page);
        // when
        serviceUnderTest.getAllExistingFields(paginationData);

        // then
        verify(fieldRepository, times(1)).findAll(any(PageRequest.class));
        verify(fieldMapper, times(1)).mapToDTOs(any(List.class));
        assertEquals(page.getTotalElements(), paginationData.getTotalEntries());
        assertEquals(page.getTotalPages(), paginationData.getTotalPages());
    }

    private FieldDTO getFieldDTO(UUID id) {
        BoundaryDTO boundaryDTO = new BoundaryDTO();
        boundaryDTO.setGeoJson(new GeoJsonDTO());
        FieldDTO fieldDTO = new FieldDTO();
        fieldDTO.setId(id);
        fieldDTO.setBoundaries(boundaryDTO);
        return fieldDTO;
    }

    private Field getField(UUID uuid) {
        Field field = new Field();
        field.setFieldUuid(uuid);
        Boundary boundary = new Boundary();
        boundary.setPolygonId("test-polygon-id");
        boundary.setCoordinates(Collections.singletonList(new Coordinate(null, 0.0, 0.0)));
        field.setBoundaries(boundary);
        return field;
    }
}