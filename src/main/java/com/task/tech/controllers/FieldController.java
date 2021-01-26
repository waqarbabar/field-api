package com.task.tech.controllers;

import com.task.tech.dtos.FieldDTO;
import com.task.tech.dtos.PaginationData;
import com.task.tech.services.FieldService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ResponseHeader;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

import static com.task.tech.constants.Constants.TAG_API;
import static com.task.tech.constants.Constants.TOTAL_ENTRIES;
import static com.task.tech.constants.Constants.TOTAL_PAGES;

@Slf4j
@SwaggerDefinition(
        consumes = {"application/json"},
        produces = {"application/json"},
        schemes = {SwaggerDefinition.Scheme.HTTP, SwaggerDefinition.Scheme.HTTPS},
        tags = @Tag(name = TAG_API, description = "Fields API"))
@Api(value = TAG_API, tags = TAG_API)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class FieldController {

    private final FieldService fieldService;

    @ApiOperation(httpMethod = "POST", value = "Add a new field", nickname = "addNewField", notes = "Add new field", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful creation"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal Server Error"),
    })
    @PostMapping("/fields")
    @ResponseStatus(HttpStatus.CREATED)
    public void addNewField(@RequestBody FieldDTO fieldDTO) {
        log.info("received request to create new field with body={}", fieldDTO);
        this.fieldService.addNewField(fieldDTO);
        log.info("completed request to create new field with id={}", fieldDTO.getId());
    }

    @ApiOperation(httpMethod = "PUT", value = "Update existing field with id", nickname = "updateField", notes = "update field", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful creation"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal Server Error"),
    })
    @PutMapping("/fields")
    @ResponseStatus(HttpStatus.OK)
    public void updateField(@RequestBody FieldDTO fieldDTO) {
        log.info("received request to update existing field with body={}", fieldDTO);
        this.fieldService.updateExistingField(fieldDTO);
        log.info("completed request to update existing field with id={}", fieldDTO.getId());
    }

    @ApiOperation(httpMethod = "DELETE", value = "Delete an existing field", nickname = "deleteField", notes = "delete field", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful creation"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal Server Error"),
    })
    @DeleteMapping("/fields/{fieldId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteField(@PathVariable UUID fieldId) {
        log.info("received request to delete existing field with id={}", fieldId);
        this.fieldService.deleteExistingField(fieldId);
        log.info("completed request to delete existing field with id={}", fieldId);
    }

    @ApiOperation(httpMethod = "GET", value = "Get field by ID", nickname = "getField", notes = "get Field", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful creation"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal Server Error"),
    })
    @GetMapping("/fields/{fieldId}")
    public FieldDTO getField(@PathVariable UUID fieldId) {
        log.info("received request to get existing field with id={}", fieldId);
        FieldDTO fieldDTO = this.fieldService.getExistingField(fieldId);
        log.info("completed request to get existing field with details={}", fieldDTO);
        return fieldDTO;
    }

    @ApiOperation(httpMethod = "GET", value = "Get all fields",
            nickname = "getAllFields",
            notes = "get All Fields",
            consumes = "application/json",
            response = FieldDTO.class,
            responseContainer = "List",
            responseHeaders = {
                    @ResponseHeader(name = TOTAL_ENTRIES, response = Integer.class, description = "Number of total entries"),
                    @ResponseHeader(name = TOTAL_PAGES, response = Integer.class, description = "Number of total pages")
            }
    )
    @ApiImplicitParams(value = {
            @ApiImplicitParam(type = "query", name = "pageNumber", defaultValue = "0", value = "Page index of total pages. Starts from 0 (Default = 0)"),
            @ApiImplicitParam(type = "query", name = "pageSize", defaultValue = "10", value = "Page size, starts from 1 (Default = 10)")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful creation"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal Server Error"),
    })
    @GetMapping("/fields")
    public ResponseEntity<List<FieldDTO>> getAllFields(@RequestParam(required = false) Integer pageNumber, @RequestParam(required = false) Integer pageSize) {
        log.info("request to get existing field(s)  with page number={} and fetch limit is={}", pageNumber, pageSize);
        PaginationData paginationData = new PaginationData(pageNumber, pageSize);
        List<FieldDTO> fieldDTOs = this.fieldService.getAllExistingFields(paginationData);
        log.info("response to get existing field(s) with size={}", fieldDTOs.size());
        return getResponse(paginationData, fieldDTOs);
    }

    private ResponseEntity<List<FieldDTO>> getResponse(PaginationData paginationData, List<FieldDTO> fieldDTOs) {
        HttpHeaders responseHeaders = new HttpHeaders();
        log.info("response header to get existing field(s) with {}={}", TOTAL_ENTRIES, paginationData.getTotalEntries());
        responseHeaders.set(TOTAL_ENTRIES, paginationData.getTotalEntries().toString());
        log.info("response header to get existing field(s) with {}={}", TOTAL_PAGES, paginationData.getTotalPages());
        responseHeaders.set(TOTAL_PAGES, paginationData.getTotalPages().toString());
        return ResponseEntity.ok().headers(responseHeaders).body(fieldDTOs);
    }
}
