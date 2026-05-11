package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.BaseUnitTest;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.RequestService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest extends BaseUnitTest {

    @MockBean
    private RequestService requestService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final long mockRequestId = 1L;

    @Test
    void shouldReturnItemRequest_whenCreated() throws Exception {

        CreateItemRequestDto createItemRequestDto = new CreateItemRequestDto();

        ItemRequestDto request = ItemRequestDto.builder().id(mockRequestId).build();

        when(requestService.createRequest(ArgumentMatchers.any(CreateItemRequestDto.class), anyLong()))
                .thenReturn(request);

        mockMvc.perform(
                post("/requests")
                        .content(objectMapper.writeValueAsString(createItemRequestDto))
                        .headers(getHeaders())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated());

        verify(requestService).createRequest(ArgumentMatchers.any(CreateItemRequestDto.class), eq(USER_ID));
    }

    @Test
    void shouldReturnOwnItemRequests_whenFindAllByRequester() throws Exception {

        List<ItemRequestDto> requests = List.of(
                ItemRequestDto.builder().description("Request 1").build(),
                ItemRequestDto.builder().description("Request 2").build()
        );

        when(requestService.findAllMyRequests(eq(USER_ID), any(Pageable.class))).thenReturn(requests);

        mockMvc.perform(
                        get("/requests")
                                .param("from", "0")
                                .param("size", "10")
                                .headers(getHeaders())
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(requests.size()))
                .andExpect(jsonPath("$[0].description").value(requests.getFirst().getDescription()))
                .andExpect(jsonPath("$[1].description").value(requests.getLast().getDescription()));

        verify(requestService).findAllMyRequests(eq(USER_ID), any(Pageable.class));
    }

    @Test
    void shouldReturnItemRequests_whenFindAll() throws Exception {

        List<ItemRequestDto> requests = List.of(
                ItemRequestDto.builder().description("Request 1").build(),
                ItemRequestDto.builder().description("Request 2").build()
        );

        when(requestService.findAllRequests(anyLong(), any(Pageable.class))).thenReturn(requests);

        mockMvc.perform(
                        get("/requests/all")
                                .param("from", "0")
                                .param("size", "10")
                                .headers(getHeaders())
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(requests.size()))
                .andExpect(jsonPath("$[0].description").value(requests.getFirst().getDescription()))
                .andExpect(jsonPath("$[1].description").value(requests.getLast().getDescription()));

        verify(requestService).findAllRequests(eq(USER_ID), any(Pageable.class));
    }

    @Test
    void shouldReturnItemRequest_whenFindById() throws Exception {

        ItemRequestDto request = ItemRequestDto.builder().id(mockRequestId).build();

        when(requestService.findRequestById(anyLong(), anyLong())).thenReturn(request);

        mockMvc.perform(
                get("/requests/{requestId}", mockRequestId)
                        .headers(getHeaders())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(mockRequestId));

        verify(requestService).findRequestById(eq(USER_ID), eq(mockRequestId));
    }

}