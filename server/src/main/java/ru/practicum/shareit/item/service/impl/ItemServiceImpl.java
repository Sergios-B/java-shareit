package ru.practicum.shareit.item.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotUserPermissionException;
import ru.practicum.shareit.item.dto.item.CreateItemDto;
import ru.practicum.shareit.item.dto.item.ItemDto;
import ru.practicum.shareit.item.dto.item.UpdateItemDto;
import ru.practicum.shareit.item.exeption.ItemNotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemShortData;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collections;
import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    private final UserService userService;

    @Override
    @Transactional
    public ItemDto createItem(CreateItemDto itemDto, Long userid) {

        User user = userService.findUserEntityByIdOrThrowAnException(userid);

        Item item = ItemMapper.toItem(itemDto, user);

        item = itemRepository.save(item);

        log.info("[ItemServiceImpl.createItem] вещь успешно создана");

        return ItemMapper.toItemDto(item);
    }

    @Override
    @Transactional
    public ItemDto updateItem(UpdateItemDto itemDto, Long userid, Long itemId) {
        Item item = findItemEntityByIdOrThrowOnException(itemId);

        if (!item.getOwner().getId().equals(userid)) {

            log.info("[ItemServiceImpl.updateItem] пользователь не является владельцем данной вещи");

            throw new NotUserPermissionException("Пользователь не является владельцем");
        }

        itemRepository.save(ItemMapper.toItemWithUpdateFields(item, itemDto));

        log.info("[ItemServiceImpl.updateItem] вещь успешно обнавлена");

        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> searchItemsByName(String name) {
        log.debug("[ItemServiceImpl.searchItemsByName] поиск по слову {}",  name);

        if (name.isEmpty()) {
            return Collections.emptyList();
        }

        return itemRepository.findAllByNameIsLikeOrDescriptionIsLike(name).stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    @Override
    public List<ItemShortData> findAllByRequestIds(List<Long> requestIds) {
        return itemRepository.findAllByRequestIdIn(requestIds);
    }

    public Item findItemEntityByIdOrThrowOnException(Long id) throws ItemNotFoundException {
        return itemRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Вещь с таким id не найден"));
    }
}
