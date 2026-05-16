package ru.practicum.shareit.request;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final RequestClient requestClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> addRequest(
            @Positive(message = "Id рользователя должно быть больше 0")
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestBody CreateItemRequestDto itemRequest
    ) {
        return requestClient.createRequest(itemRequest, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getMyRequests(
            @Positive(message = "Id рользователя должно быть больше 0")
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size
    ) {
        return requestClient.findAllMyRequests(userId, from, size);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getRequests(
            @Positive(message = "Id рользователя должно быть больше 0")
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size
    ) {
        return requestClient.findAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(
            @Positive(message = "Id пользователя должно быть больше 0")
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @Positive(message = "Id запроса рользователя должно быть больше 0")
            @PathVariable Long requestId
    ) {
        return requestClient.findRequestById(userId, requestId);
    }
}
