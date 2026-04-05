package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import java.util.Collection;

public interface ItemService {
    ItemDto create(Long userId, ItemDto itemDto);
    ItemDto update(Long userId, Long itemId, ItemDto itemDto);
    ItemDto findById(Long itemId);
    Collection<ItemDto> findAllByOwner(Long userId);
    Collection<ItemDto> search(String text);
}