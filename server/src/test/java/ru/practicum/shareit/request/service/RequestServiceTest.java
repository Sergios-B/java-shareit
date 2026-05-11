package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.BaseUnitTest;
import ru.practicum.shareit.MockGeneratorTest;
import ru.practicum.shareit.item.model.ItemShortData;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.exeption.ItemRequestNotFoundException;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.request.service.impl.RequestServiceImpl;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequestServiceTest extends BaseUnitTest {

    @Mock
    private UserService userService;

    @Mock
    private ItemService itemService;

    @Mock
    private RequestRepository requestRepository;

    @InjectMocks
    private RequestServiceImpl requestService;

    private User user;

    private final Long requestId = 1L;

    @BeforeEach
    void setUp() {
        user = User.builder().id(USER_ID).name("Petia").build();
    }

    @Test
    void should_returnException_whenNotAuthorized() {

        CreateItemRequestDto requestDto = new CreateItemRequestDto();
        requestDto.setDescription(MockGeneratorTest.generatorText(20));

        when(userService.findUserEntityByIdOrThrowAnException(anyLong())).thenThrow(UserNotFoundException.class);

        Assertions.assertThrows(UserNotFoundException.class, () -> requestService.createRequest(requestDto, USER_ID));

        verify(requestRepository, never()).save(any());
    }

    @Test
    void should_returnRequest_whenItemRequestCreated() {

        String expectedRequestText = MockGeneratorTest.generatorText(20);
        long expectedRequestId = 1L;

        CreateItemRequestDto requestDto = new CreateItemRequestDto();
        requestDto.setDescription(expectedRequestText);

        ItemRequest itemRequest = ItemRequest.builder().id(expectedRequestId).description(expectedRequestText).build();

        when(userService.findUserEntityByIdOrThrowAnException(anyLong())).thenReturn(user);
        when(requestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);

        ItemRequestDto savedRequest = requestService.createRequest(requestDto, USER_ID);

        assertThat(savedRequest)
                .isNotNull()
                .hasFieldOrPropertyWithValue("description", expectedRequestText)
                .hasFieldOrPropertyWithValue("id", expectedRequestId);
    }

    @Test
    void should_returnRequestsList_whenFindByUserId() {

        int expectedRequestCount = 2;

        List<ItemRequest> requests = List.of(
                ItemRequest.builder().id(1L).requestorId(USER_ID).build(),
                ItemRequest.builder().id(2L).requestorId(USER_ID).build()
        );

        ItemShortData itemData = getItemData();

        when(userService.findUserEntityByIdOrThrowAnException(USER_ID)).thenReturn(user);
        when(requestRepository.findItemRequestsByRequestorIdWithItems(USER_ID)).thenReturn(requests);
        when(itemService.findAllByRequestIds(anyList())).thenReturn(List.of(itemData));

        List<ItemRequestDto> allMyRequests = requestService.findAllMyRequests(USER_ID, Pageable.unpaged());


        Assertions.assertEquals(expectedRequestCount, allMyRequests.size());
    }

    private ItemShortData getItemData() {
        ItemShortData itemData = mock(ItemShortData.class);
        lenient().when(itemData.getId()).thenReturn(new Random().nextLong(10));
        lenient().when(itemData.getName()).thenReturn("Дрель");
        lenient().when(itemData.getRequestId()).thenReturn(requestId);
        lenient().when(itemData.getOwner()).thenReturn(USER_ID);

        return itemData;
    }

    @Test
    void should_returnRequestsListAll() {

        int expectedRequestCount = 2;

        List<ItemRequest> requests = List.of(
                ItemRequest.builder().id(1L).requestorId(USER_ID).build(),
                ItemRequest.builder().id(2L).requestorId(USER_ID).build()
        );

        Pageable pageable = Pageable.ofSize(requests.size());

        when(userService.findUserEntityByIdOrThrowAnException(USER_ID)).thenReturn(user);
        when(requestRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(requests, pageable, requests.size()));

        List<ItemRequestDto> allRequests = requestService.findAllRequests(USER_ID, Pageable.unpaged());

        assertThat(allRequests).isNotNull();
        Assertions.assertEquals(expectedRequestCount, allRequests.size());
    }

    @Test
    void should_returnException_whenRequestByIdNotFound() {

        when(userService.findUserEntityByIdOrThrowAnException(USER_ID)).thenReturn(user);
        when(requestRepository.findItemRequestByWithItems(anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(ItemRequestNotFoundException.class, () ->
                requestService.findRequestById(USER_ID, requestId));
    }

    @Test
    void should_returnItemRequest_whenItemRequestFound() {

        ItemRequest itemRequest = ItemRequest.builder().id(requestId).requestorId(USER_ID).build();

        List<ItemShortData> items = List.of(
                getItemData(),
                getItemData()
        );

        when(userService.findUserEntityByIdOrThrowAnException(USER_ID)).thenReturn(user);
        when(requestRepository.findItemRequestByWithItems(anyLong())).thenReturn(Optional.ofNullable(itemRequest));
        when(itemService.findAllByRequestIds(anyList())).thenReturn(items);

        ItemRequestDto requestById = requestService.findRequestById(USER_ID, requestId);

        System.out.println(requestById.getId());

        assertThat(requestById)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", requestId);

        verify(userService).findUserEntityByIdOrThrowAnException(USER_ID);
    }
}