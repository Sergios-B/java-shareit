package ru.practicum.shareit.request.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page; // ИСПРАВЛЕНО: Правильный импорт Page
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.item.ItemForRequestDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.ItemShortData;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.exeption.ItemRequestNotFoundException;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final UserService userService;
    private final ItemService itemService;

    @Override
    @Transactional
    public ItemRequestDto createRequest(CreateItemRequestDto createRequest, Long userId) {
        userService.findUserEntityByIdOrThrowAnException(userId);

        ItemRequest request = RequestMapper.toItemRequest(createRequest);
        request.setRequestorId(userId);
        request = requestRepository.save(request);

        log.info("[RequestServiceImpl.createRequest] пользователь добавил запрос на вещь");
        return RequestMapper.toRequestDto(request);
    }

    @Override
    public List<ItemRequestDto> findAllMyRequests(Long userId) {
        userService.findUserEntityByIdOrThrowAnException(userId);

        List<ItemRequest> requests = requestRepository.findItemRequestsByRequestorIdWithItems(userId);

        List<ItemShortData> items = itemService
                .findAllByRequestIds(requests.stream().map(ItemRequest::getId).toList());

        Map<Long, List<ItemShortData>> itemByRequest = items.stream()
                .collect(Collectors.groupingBy(ItemShortData::getRequestId));

        return requests.stream()
                .map(request ->
                        RequestMapper.toRequestDto(
                                request, itemByRequest.getOrDefault(request.getId(), new ArrayList<>())))
                .toList();
    }

    @Override
    public List<ItemRequestDto> findAllRequests(Long userId, Pageable pageable) {
        userService.findUserEntityByIdOrThrowAnException(userId);

        Page<ItemRequest> requestsPage = requestRepository.findAllByRequestorIdNot(userId, pageable);
        List<ItemRequest> requests = requestsPage.getContent();

        if (requests.isEmpty()) {
            return List.of();
        }

        List<Long> requestIds = requests.stream()
                .map(ItemRequest::getId)
                .toList();

        List<ItemShortData> itemsShort = itemService.findAllByRequestIds(requestIds);

        Map<Long, List<ItemShortData>> itemsByRequestId = itemsShort.stream()
                .collect(Collectors.groupingBy(ItemShortData::getRequestId));

        return requests.stream()
                .map(request -> {
                    ItemRequestDto dto = RequestMapper.toRequestDto(request);

                    List<ItemShortData> shortDataList = itemsByRequestId.getOrDefault(request.getId(), List.of());

                    List<ItemForRequestDto> itemDtos = shortDataList.stream()
                            .map(ItemMapper::toItemForRequestDto)
                            .toList();

                    dto.setItems(itemDtos);
                    return dto;
                })
                .toList();
    }

    @Override
    public ItemRequestDto findRequestById(Long userId, Long requestId) {
        userService.findUserEntityByIdOrThrowAnException(userId);

        Optional<ItemRequest> request = requestRepository.findItemRequestByWithItems(requestId);

        if (request.isPresent()) {
            ItemRequest finedRequest = request.get();

            List<ItemShortData> items = itemService.findAllByRequestIds(List.of(finedRequest.getId()));

            return RequestMapper.toRequestDto(finedRequest, items);
        }

        log.info("[RequestServiceImpl.findRequestById] запрос с таким id не найден");
        throw new ItemRequestNotFoundException("Запрос с таким идентификатором не был найден");
    }
}
