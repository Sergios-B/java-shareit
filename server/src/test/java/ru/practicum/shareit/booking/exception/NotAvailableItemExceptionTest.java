package ru.practicum.shareit.booking.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NotAvailableItemExceptionTest {

    @Test
    void should_returnExceptionMessage() {

        String message = "Not available item";

        NotAvailableItemException exception = new NotAvailableItemException(message);

        assertEquals(message, exception.getMessage(), "Сообщение должно совпадать с переданным в конструктор");
    }
}