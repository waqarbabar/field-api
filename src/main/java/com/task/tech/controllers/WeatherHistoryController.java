package com.task.tech.controllers;

import com.task.tech.dtos.WeatherHistoryDTO;
import com.task.tech.services.WeatherHistoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static com.task.tech.constants.Constants.TAG_API;


@SwaggerDefinition(
        consumes = {"application/json"},
        produces = {"application/json"},
        schemes = {SwaggerDefinition.Scheme.HTTP, SwaggerDefinition.Scheme.HTTPS},
        tags = @Tag(name = TAG_API, description = "Fields API"))
@Api(value = TAG_API, tags = TAG_API)
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class WeatherHistoryController {

    private final WeatherHistoryService weatherHistoryService;

    @ApiOperation(httpMethod = "GET", value = "Get weather details of a field", nickname = "getFieldWeather", notes = "get weather details", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful creation"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal Server Error"),
    })
    @GetMapping("/fields/{fieldId}/weather")
    public WeatherHistoryDTO getFieldWeatherHistory(@PathVariable UUID fieldId) {
        log.debug("received request to get weather history of the field with id={}", fieldId);
        WeatherHistoryDTO weatherHistoryDTO = this.weatherHistoryService.getWeatherHistory(fieldId);
        log.debug("completed request to to get weather history with data size={}, of field id={}", weatherHistoryDTO.getWeather().size(), fieldId);
        return weatherHistoryDTO;
    }
}
