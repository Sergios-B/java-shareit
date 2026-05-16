package ru.practicum.shareit.item.dto.item;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateItemDto {

    private String name;

    private String description;

    private Boolean available;

    Long requestId;
}
