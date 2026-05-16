package ru.practicum.shareit.item.dto.comment;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCommentDto {
    @NotBlank(message = "Поле 'text' не должно быть пустым")
    private String text;
}