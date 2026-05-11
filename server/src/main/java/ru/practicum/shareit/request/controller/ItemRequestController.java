package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
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
public class ItemRequestController {

    private final RequestService requestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemRequestDto addRequest(
            @RequestBody CreateItemRequestDto itemRequest,
            @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        return requestService.createRequest(itemRequest, userId);
    }

    @GetMapping
    public List<ItemRequestDto> getMyRequests(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam Integer from,
            @RequestParam Integer size
    ) {
        Pageable pageable = PageRequest.of(from, size);
        return requestService.findAllMyRequests(userId, pageable);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getRequests(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam Integer from,
            @RequestParam Integer size
    ) {
        Pageable pageable = PageRequest.of(from, size, Sort.by(Sort.Direction.ASC, "createdAt"));
        return requestService.findAllRequests(userId, pageable);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long requestId
    ) {
        return requestService.findRequestById(userId, requestId);
    }
}
