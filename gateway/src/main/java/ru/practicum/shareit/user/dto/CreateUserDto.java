package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class CreateUserDto {

    @NotEmpty(message = "Поле 'name' не должно быть пустым")
    private String name;

    @NotEmpty(message = "Поле 'email' не должно быть пустым")
    @Email(message = "Не валидный email")
    private String email;
}
