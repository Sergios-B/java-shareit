package ru.practicum.shareit.user.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EmailAlreadyUserExceptionTest {

    @Test
    void should_returnExceptionMessage() {

        String message = "Email is already in use";

        EmailAlreadyUserException exception = new EmailAlreadyUserException(message);

        assertEquals(message, exception.getMessage(), "Сообщение должно совпадать с переданным в конструктор");
    }
}