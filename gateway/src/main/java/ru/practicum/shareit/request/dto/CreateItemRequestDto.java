package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateItemRequestDto {
    @NotBlank(message = "Описания запроса не должен быть пустым")
    private String description;
}
