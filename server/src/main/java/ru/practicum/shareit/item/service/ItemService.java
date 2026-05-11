package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.item.CreateItemDto;
import ru.practicum.shareit.item.dto.item.ItemDto;
import ru.practicum.shareit.item.dto.item.UpdateItemDto;
import ru.practicum.shareit.item.exeption.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemShortData;

import java.util.List;

public interface ItemService {

    Item findItemEntityByIdOrThrowOnException(Long id) throws ItemNotFoundException;

    ItemDto createItem(CreateItemDto itemDto, Long userid);

    ItemDto updateItem(UpdateItemDto itemDto, Long userid, Long itemId);

    List<ItemDto> searchItemsByName(String name);

    List<ItemShortData> findAllByRequestIds(List<Long> requestIds);
}
