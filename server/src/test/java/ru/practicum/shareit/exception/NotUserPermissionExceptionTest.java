package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NotUserPermissionExceptionTest {

    @Test
    void shouldStoreMessage() {

        String message = "User not permission";

        NotUserPermissionException exception = new NotUserPermissionException(message);

        assertEquals(message, exception.getMessage(),
                "Сообщение должно совпадать с переданным в конструктор");
    }
}