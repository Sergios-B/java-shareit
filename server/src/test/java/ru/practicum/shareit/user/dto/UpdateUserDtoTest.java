package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

class UpdateUserDtoTest {

    @Test
    void should_returnFalse_whenNameOrEmailIsNull() {

        UpdateUserDto updateUserDto = new UpdateUserDto();

        assertFalse(updateUserDto.hasName());
        assertFalse(updateUserDto.hasEmail());

        updateUserDto.setName("");
        updateUserDto.setEmail("");

        assertFalse(updateUserDto.hasName());
        assertFalse(updateUserDto.hasEmail());
    }
}