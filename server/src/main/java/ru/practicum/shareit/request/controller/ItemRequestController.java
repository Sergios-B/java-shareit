package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.RequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {

    private final RequestService requestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemRequestDto addRequest(
            @RequestBody CreateItemRequestDto itemRequest,
            @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        log.info("[ItemRequestController.addRequest] пользователь id={} добавляет запрос", userId);
        return requestService.createRequest(itemRequest, userId);
    }

    @GetMapping
    public List<ItemRequestDto> getMyRequests(
            @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        log.info("[ItemRequestController.getMyRequests] запрос собственных запросов пользователя id={}", userId);
        return requestService.findAllMyRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getRequests(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(name = "from", defaultValue = "0") int from,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        if (from < 0 || size <= 0) {
            throw new IllegalArgumentException("Некорректные параметры пагинации: from не может быть < 0, size должен быть > 0");
        }

        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        log.info("[ItemRequestController.getRequests] запрос чужих запросов. from={}, size={}, page={}", from, size, from / size);
        return requestService.findAllRequests(userId, pageable);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long requestId
    ) {
        log.info("[ItemRequestController.getRequestById] запрос данных по конкретному запросу id={} от пользователя id={}", requestId, userId);
        return requestService.findRequestById(userId, requestId);
    }
}
