package ru.practicum.shareit.item.dto.item;

public record UpdateItemDto(
        String name,
        String description,
        Boolean available,
        Long request
) {
   public UpdateItemDto() {
       this(null, null, null, null);
   }
}
