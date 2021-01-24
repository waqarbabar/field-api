package com.task.tech.dtos;

import lombok.Getter;

import javax.validation.constraints.Min;
import java.util.Optional;

import static com.task.tech.constants.Constants.DEFAULT_PAGE_NUMBER;
import static com.task.tech.constants.Constants.DEFAULT_PAGE_SIZE;

@Getter
public class PaginationDTO {
    @Min(value = 0, message = "pageNumber cannot be negative")
    private final Integer pageNumber;
    @Min(value = 1, message = "pageSize cannot be less than 1")
    private final Integer pageSize;

    public PaginationDTO(Integer pageNumber, Integer pageSize) {
        this.pageNumber = Optional.ofNullable(pageNumber).orElse(DEFAULT_PAGE_NUMBER);
        this.pageSize = Optional.ofNullable(pageSize).orElse(DEFAULT_PAGE_SIZE);
    }
}