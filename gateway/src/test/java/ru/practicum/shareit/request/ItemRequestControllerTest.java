package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.BaseUnitTest;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest extends BaseUnitTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    private RequestClient requestClient;

    @Test
    void shouldReturnStatus404_whenHeaderUserIdNotFound() throws Exception {

        CreateItemRequestDto createItemRequestDto = new CreateItemRequestDto();

        mockMvc.perform(
                post("/requests")
                        .content(objectMapper.writeValueAsString(createItemRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").isNotEmpty())
                .andExpect(jsonPath("$.error").value(NOT_MISSING_HEADER));

        verify(requestClient, never()).createRequest(createItemRequestDto, USER_ID);
    }

    @Test
    void shouldReturnStatus201_whenRequestCreated() throws Exception {

        CreateItemRequestDto createItemRequestDto = new CreateItemRequestDto();
        createItemRequestDto.setDescription("Description text");

        when(requestClient.createRequest(any(CreateItemRequestDto.class), anyLong()))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).build());

        mockMvc.perform(
                post("/requests")
                        .content(objectMapper.writeValueAsString(createItemRequestDto))
                        .headers(getHeaders())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isCreated());

        verify(requestClient).createRequest(createItemRequestDto, USER_ID);
    }

    @Test
    void shouldReturnStatus200AndNotEmptyList_whenGetMyRequests() throws Exception {

        when(requestClient.findAllMyRequests(anyLong(), anyInt(), anyInt()))
                .thenReturn(ResponseEntity.ok().body(List.of("Request 1", "Request 2")));

        mockMvc.perform(
                        get("/requests")
                                .headers(getHeaders())
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        verify(requestClient).findAllMyRequests(anyLong(), anyInt(), anyInt());
    }

    @Test
    void shouldReturnStatus200AndNotEmptyList_whenGetAllRequests() throws Exception {

        when(requestClient.findAllRequests(anyLong(), anyInt(), anyInt()))
                .thenReturn(ResponseEntity.ok().body(List.of("Request 1", "Request 2", "Request 3")));

        mockMvc.perform(
                        get("/requests/all")
                                .headers(getHeaders())
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3));

        verify(requestClient).findAllRequests(anyLong(), anyInt(), anyInt());
    }
}