package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final UserService userService;
    private final Map<Long, Item> items = new HashMap<>();
    private long nextId = 1;

    @Override
    public ItemDto create(Long userId, ItemDto itemDto) {
        UserDto ownerDto = userService.findById(userId);
        Item item = ItemMapper.toItem(itemDto);
        item.setId(nextId++);
        item.setOwner(UserMapper.toUser(ownerDto));
        items.put(item.getId(), item);
        log.info("Создана вещь с id: {} для владельца: {}", item.getId(), userId);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        Item item = items.get(itemId);
        if (item == null) {
            throw new NotFoundException("Вещь не найдена");
        }
        if (!item.getOwner().getId().equals(userId)) {
            throw new AccessDeniedException("Доступ запрещен");
        }
        if (itemDto.getName() != null && !itemDto.getName().isBlank()) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null && !itemDto.getDescription().isBlank()) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        log.info("Обновлена вещь с id: {}", itemId);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto findById(Long userId, Long itemId) {
        Item item = items.get(itemId);
        if (item == null) {
            throw new NotFoundException("Вещь не найдена");
        }
        return ItemMapper.toItemDto(item);
    }

    @Override
    public Collection<ItemDto> findAllByOwner(Long userId) {
        return items.values().stream()
                .filter(item -> item.getOwner().getId().equals(userId))
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ItemDto> search(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        String query = text.toLowerCase();
        return items.values().stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(query)
                        || item.getDescription().toLowerCase().contains(query))
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}