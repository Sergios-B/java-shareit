package ru.practicum.shareit.item.exeption;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.exeption.ItemRequestNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

class ItemNotFoundExceptionTest {

    @Test
    void shouldStoreMessage() {

        String message = "Item request not found";

        ItemRequestNotFoundException exception = new ItemRequestNotFoundException(message);

        assertEquals(message, exception.getMessage(),
                "Сообщение должно совпадать с переданным в конструктор");
    }
}