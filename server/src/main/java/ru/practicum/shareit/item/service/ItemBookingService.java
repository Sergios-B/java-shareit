package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.item.ItemToOwnerDto;

import java.util.List;

public interface ItemBookingService {

    List<ItemToOwnerDto> findItemsWithAfterAndBeforeBookingDateByUserId(Long userId);

    ItemToOwnerDto getItemById(Long id);
}
