package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.comment.CreateCommentDto;
import ru.practicum.shareit.item.dto.item.CreateItemDto;
import ru.practicum.shareit.item.dto.item.UpdateItemDto;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Object addItem(
            @RequestBody @Valid CreateItemDto itemDto,
            @Positive(message = "Id пользователя должно быть больше 0")
            @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        log.info("Gateway: addItem user={}, dto={}", userId, itemDto);
        return itemClient.createItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public Object updateItem(
            @RequestBody UpdateItemDto itemDto,
            @Positive(message = "Id пользователя должно быть больше 0")
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @Positive(message = "Id должно быть больше 0") @PathVariable Long itemId
    ) {
        log.info("Gateway: updateItem item={}, user={}, dto={}", itemId, userId, itemDto);
        return itemClient.updateItem(itemDto, userId, itemId);
    }

    @GetMapping("/{itemId}")
    public Object findItemById(
            @Positive(message = "Id должно быть больше 0")
            @PathVariable Long itemId,
            @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        log.info("Gateway: findItemById item={}, user={}", itemId, userId);
        return itemClient.getItemById(itemId, userId); // ИСПРАВЛЕНО: передаем userId
    }

    @GetMapping("/search")
    public Object searchActualItems(
            @RequestParam(required = false, defaultValue = "") String text,
            @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        log.info("Gateway: searchActualItems user={}, text={}", userId, text);
        return itemClient.searchItemsByName(userId, text);
    }

    @GetMapping
    public Object findItemsByUserId(
            @Positive(message = "Id пользователя должно быть больше 0")
            @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        log.info("Gateway: findItemsByUserId user={}", userId);
        return itemClient.findItemsWithAfterAndBeforeBookingDateByUserId(userId);
    }

    @PostMapping("/{itemId}/comment")
    public Object addComment(
            @Positive(message = "Id должно быть больше 0")
            @PathVariable Long itemId,
            @Positive(message = "Id пользователя должно быть больше 0")
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @Valid @RequestBody CreateCommentDto commentDto
    ) {
        log.info("Gateway: addComment item={}, user={}, dto={}", itemId, userId, commentDto);
        return itemClient.addComment(itemId, userId, commentDto);
    }
}