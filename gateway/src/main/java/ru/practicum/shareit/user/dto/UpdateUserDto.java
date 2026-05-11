package ru.practicum.shareit.user.dto;

public record UpdateUserDto(
        String name,
        String email
) {
    public UpdateUserDto() {
        this(null, null);
    }
}
