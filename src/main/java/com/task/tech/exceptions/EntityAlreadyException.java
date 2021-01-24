package com.task.tech.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class EntityAlreadyException extends RuntimeException {
    public EntityAlreadyException(String message) {
        super(message);
    }
}
