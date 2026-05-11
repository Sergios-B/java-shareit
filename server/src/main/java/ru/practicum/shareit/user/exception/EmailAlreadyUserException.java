package ru.practicum.shareit.user.exception;

public class EmailAlreadyUserException extends RuntimeException {

    public EmailAlreadyUserException(String message) {
        super(message);
    }
}
