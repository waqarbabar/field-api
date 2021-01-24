package com.task.tech.controllers;

import com.task.tech.dtos.FieldDTO;
import com.task.tech.dtos.PaginationDTO;
import com.task.tech.mappers.FieldMapper;
import com.task.tech.services.FieldService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ResponseHeader;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
        this.fieldService.addNewField(fieldDTO);
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
        this.fieldService.updateExistingField(fieldDTO);
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
        this.fieldService.deleteExistingField(fieldId);
    }

    @ApiOperation(httpMethod = "GET", value = "Get field by ID", nickname = "getField", notes = "get Field", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful creation"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal Server Error"),
    })
    @GetMapping("/fields/{fieldId}")
    public FieldDTO getField(@PathVariable UUID fieldId) {
        return this.fieldService.getExistingField(fieldId);
    }

    @ApiOperation(httpMethod = "GET", value = "Get all fields",
            nickname = "getAllFields",
            notes = "get All Fields",
            consumes = "application/json",
            response = FieldDTO.class,
            responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful creation"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal Server Error"),
    })
    @GetMapping("/fields")
    public List<FieldDTO> getAllFields(
            @ApiParam(type = "query", name = "pageNumber", defaultValue = "0", value = "Page index of total pages. Starts from 0 (Default = 0)") @RequestParam Integer pageNumber,
            @ApiParam(type = "query", name = "pageSize", defaultValue = "10", value = "Page size, starts from 1 (Default = 10") @RequestParam Integer pageSize) {
        return this.fieldService.getAllExistingFields(new PaginationDTO(pageNumber, pageSize));
    }
}
