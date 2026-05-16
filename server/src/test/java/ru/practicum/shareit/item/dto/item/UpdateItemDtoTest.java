package ru.practicum.shareit.item.dto.item;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UpdateItemDtoTest {

    @Test
    void should_returnFalse_whenNameOrDescriptionIsNull() {

        UpdateItemDto updateItemDto = new UpdateItemDto();

        assertFalse(updateItemDto.hasName());
        assertFalse(updateItemDto.hasDescription());

        updateItemDto.setName("");
        updateItemDto.setDescription("");

        assertFalse(updateItemDto.hasName());
        assertFalse(updateItemDto.hasDescription());
    }

    @Test
    void should_returnFalse_whenAvailableIsNull() {

        UpdateItemDto updateItemDto = new UpdateItemDto();

        assertFalse(updateItemDto.hasAvailable());
    }
}