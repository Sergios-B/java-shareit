package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.comment.CommentDto;
import ru.practicum.shareit.item.dto.comment.CreateCommentDto;
import ru.practicum.shareit.item.dto.item.CreateItemDto;
import ru.practicum.shareit.item.dto.item.ItemDto;
import ru.practicum.shareit.item.dto.item.ItemToOwnerDto;
import ru.practicum.shareit.item.dto.item.UpdateItemDto;
import ru.practicum.shareit.item.service.CommentService;
import ru.practicum.shareit.item.service.ItemBookingService;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemService itemService;
    private final ItemBookingService itemBookingService;
    private final CommentService commentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto addItem(
            @RequestBody CreateItemDto itemDto,
            @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        log.info("[ItemController.addItem] добавление вещи пользователем id={}", userId);
        return itemService.createItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(
            @RequestBody UpdateItemDto itemDto,
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long itemId
    ) {
        log.info("[ItemController.updateItem] обновление вещи id={} пользователем id={}", itemId, userId);
        return itemService.updateItem(itemDto, userId, itemId);
    }

    @GetMapping("/{itemId}")
    public ItemToOwnerDto findItemById(
            @PathVariable Long itemId
    ) {
        log.info("[ItemController.findItemById] запрос вещи id={}", itemId);
        return itemBookingService.getItemById(itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchActualItems(
            @RequestParam(required = false, defaultValue = "") String text
    ) {
        log.info("[ItemController.searchActualItems] поиск вещей по строке: {}", text);
        return itemService.searchItemsByName(text);
    }

    @GetMapping
    public List<ItemToOwnerDto> findItemsByUserId(
            @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        log.info("[ItemController.findItemsByUserId] запрос всех вещей владельца id={}", userId);
        return itemBookingService.findItemsWithAfterAndBeforeBookingDateByUserId(userId);
    }

    @PostMapping("/{itemId}/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto addComment(
            @PathVariable Long itemId,
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestBody CreateCommentDto commentDto
    ) {
        log.info("[ItemController.addComment] добавление отзыва к вещи id={} от пользователя id={}", itemId, userId);
        return commentService.addComment(itemId, userId, commentDto);
    }
}
