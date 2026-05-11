package ru.practicum.shareit.request.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.ItemShortData;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RequestMapper {

    public static ItemRequestDto toRequestDto(ItemRequest createRequest, List<ItemShortData> items) {
        return ItemRequestDto.builder()
                .id(createRequest.getId())
                .description(createRequest.getDescription())
                .created(createRequest.getCreatedAt())
                .items(items)
                .build();
    }

    public static ItemRequestDto toRequestDto(ItemRequest createRequest) {
        return toRequestDto(createRequest, null);
    }

    public static ItemRequest toItemRequest(CreateItemRequestDto createRequestDto) {
        return ItemRequest.builder()
                .description(createRequestDto.getDescription())
                .createdAt(LocalDateTime.now())
                .build();
    }
}
