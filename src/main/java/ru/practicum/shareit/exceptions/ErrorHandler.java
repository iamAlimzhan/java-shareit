package ru.practicum.shareit.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(final NotFoundException ex) {
        log.debug("Обработка NotFoundException со статусом 404 Not Found: {}", ex.getMessage(), ex);
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler({
            ValidationException.class,
            MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(final Exception ex) {
        log.debug("Обработка ValidationException со статусом 400 Bad Request: {}", ex.getMessage(), ex);
        return new ErrorResponse(String.format("Ошибка с полем \"%s\"", ex.getMessage()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleEmailException(final EmailException ex) {
        log.debug("Обработка EmailException со статусом 409 Conflict: {}", ex.getMessage(), ex);
        return new ErrorResponse(String.format("Пользователь уже существует с email \"%s\"", ex.getParam()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowable(final Throwable ex) {
        log.error("Обработка Throwable со статусом 500 Internal Server Error: {}", ex.getMessage(), ex);
        return new ErrorResponse(ex.getMessage());
    }
}
