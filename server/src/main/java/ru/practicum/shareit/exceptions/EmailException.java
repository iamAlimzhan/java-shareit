package ru.practicum.shareit.exceptions;

import lombok.Getter;

@Getter
public class EmailException extends RuntimeException {
    private final String param;

    public EmailException(String param) {
        this.param = param;
    }
}
