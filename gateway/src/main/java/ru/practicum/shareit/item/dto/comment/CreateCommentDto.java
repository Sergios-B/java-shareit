package ru.practicum.shareit.item.dto.comment;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCommentDto {
    @NotEmpty(message = "Поле 'text' не должно быть пустым")
    private String text;
}
