package ru.practicum.shareit.exceptions;

import lombok.Getter;

@Getter
public class RestException {
    private final String error;

    public RestException(String error) {
        this.error = error;
    }
}
