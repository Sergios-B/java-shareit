package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ItemDto create(@RequestHeader(USER_ID_HEADER) Long userId,
                          @Valid @RequestBody ItemDto itemDto) {
        log.info("Запрос на создание вещи '{}' от пользователя {}", itemDto.getName(), userId);
        return itemService.create(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader(USER_ID_HEADER) Long userId,
                          @PathVariable Long itemId,
                          @RequestBody ItemDto itemDto) {
        log.info("Запрос на обновление вещи с id: {} от пользователя {}", itemId, userId);
        return itemService.update(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto findById(@RequestHeader(USER_ID_HEADER) Long userId, @PathVariable Long itemId) {
        log.info("Запрос вещи с id: {} от пользователя {}", itemId, userId);
        return itemService.findById(userId, itemId);
    }

    @GetMapping
    public Collection<ItemDto> findAllByOwner(@RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("Запрос всех вещей владельца с id: {}", userId);
        return itemService.findAllByOwner(userId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> search(@RequestParam String text) {
        log.info("Поиск вещей по запросу: '{}'", text);
        return itemService.search(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(USER_ID_HEADER) Long userId,
                                 @PathVariable Long itemId,
                                 @Valid @RequestBody CommentDto commentDto) {
        log.info("Пользователь {} оставляет отзыв на вещь {}", userId, itemId);
        return itemService.addComment(userId, itemId, commentDto);
    }
}