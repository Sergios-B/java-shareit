package ru.practicum.shareit.exception;

public class NotUserPermissionException extends RuntimeException {
    public NotUserPermissionException(String message) {
        super(message);
    }
}
