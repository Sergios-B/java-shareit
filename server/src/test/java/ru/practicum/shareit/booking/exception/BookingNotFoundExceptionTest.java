package ru.practicum.shareit.booking.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BookingNotFoundExceptionTest {

    @Test
    void should_returnExceptionMessage() {

        String message = "Booking not found";

        BookingNotFoundException exception = new BookingNotFoundException(message);

        assertEquals(message, exception.getMessage(), "Сообщение должно совпадать с переданным в конструктор");
    }
}