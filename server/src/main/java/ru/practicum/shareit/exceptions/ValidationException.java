package ru.practicum.shareit.exceptions;

import lombok.Getter;

@Getter
public class ValidationException extends RuntimeException {
    private final String param;

    public ValidationException(String param) {
        this.param = param;
    }
}
