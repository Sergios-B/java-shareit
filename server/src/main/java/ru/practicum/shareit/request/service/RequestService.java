package ru.practicum.shareit.request.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface RequestService {
    ItemRequestDto createRequest(CreateItemRequestDto itemRequest, Long userId);

    List<ItemRequestDto> findAllMyRequests(Long userId, Pageable pageable);

    List<ItemRequestDto> findAllRequests(Long userId, Pageable pageable);

    ItemRequestDto findRequestById(Long userId, Long requestId);
}
