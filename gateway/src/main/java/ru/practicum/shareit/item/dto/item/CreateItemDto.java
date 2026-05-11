package ru.practicum.shareit.item.dto.item;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CreateItemDto {

    @NotEmpty(message = "Поле 'name' не должно быть пустым")
    private String name;

    @NotEmpty(message = "Поле 'description' не должно быть пустым")
    private String description;

    @NotNull(message = "Поле 'available' обязательное")
    private Boolean available;

    private Long requestId;
}
