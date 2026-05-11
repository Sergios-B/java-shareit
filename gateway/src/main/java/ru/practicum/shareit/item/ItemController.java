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
        return itemClient.createItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public Object updateItem(
            @RequestBody UpdateItemDto itemDto,
            @Positive(message = "Id пользователя должно быть больше 0")
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @Positive(message = "Id должно быть больше 0") @PathVariable Long itemId
    ) {
        return itemClient.updateItem(itemDto, userId, itemId);
    }

    @GetMapping("/{itemId}")
    public Object findItemById(
            @Positive(message = "Id должно быть больше 0")
            @PathVariable Long itemId
    ) {
        return itemClient.getItemById(itemId);
    }

    @GetMapping("/search")
    public Object searchActualItems(
            @RequestParam(required = false, defaultValue = "") String text,
            @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        return itemClient.searchItemsByName(userId, text);
    }

    @GetMapping
    public Object findItemsByUserId(
            @Positive(message = "Id пользователя должно быть больше 0")
            @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
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
        return itemClient.addComment(itemId, userId, commentDto);
    }
}
