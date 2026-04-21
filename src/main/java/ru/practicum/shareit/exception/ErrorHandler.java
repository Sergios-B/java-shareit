package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.Objects;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFound(final NotFoundException e) {
        log.error("Ошибка 404: {}", e.getMessage());
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleConflict(final ConflictException e) {
        log.error("Ошибка 409: {}", e.getMessage());
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Map<String, String> handleAccessDenied(final AccessDeniedException e) {
        log.error("Ошибка 403: {}", e.getMessage());
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidation(final MethodArgumentNotValidException e) {
        String defaultMessage = e.getBindingResult().getAllErrors().getFirst().getDefaultMessage();
        String message = Objects.requireNonNullElse(defaultMessage, "Ошибка валидации");
        log.error("Ошибка валидации 400: {}", message);
        return Map.of("error", message);
    }

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleOtherExceptions(final Throwable e) {
        log.error("Непредвиденная ошибка 500: ", e);
        return Map.of("error", "Произошла непредвиденная ошибка: " + e.getMessage());
    }
}