package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import ru.practicum.shareit.BaseUnitTest;
import ru.practicum.shareit.item.dto.comment.CreateCommentDto;
import ru.practicum.shareit.item.dto.item.CreateItemDto;
import ru.practicum.shareit.item.dto.item.UpdateItemDto;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest extends BaseUnitTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    private ItemClient itemClient;

    private final long itemId = 1L;

    @Test
    void shouldReturnStatus404_whenUserHeaderIsMissing() throws Exception {

        CreateItemDto createItemDto = new CreateItemDto();

        mockMvc.perform(
                        post("/items")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(createItemDto))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").isNotEmpty())
                .andExpect(jsonPath("$.error").value(NOT_MISSING_HEADER));

        verify(itemClient, never()).createItem(createItemDto, USER_ID);
    }

    @Test
    void shouldReturnStatus201_whenItemCreated() throws Exception {

        CreateItemDto createItemDto = new CreateItemDto();
        createItemDto.setName("test name");
        createItemDto.setDescription("description");
        createItemDto.setAvailable(true);

        when(itemClient.createItem(any(CreateItemDto.class), anyLong()))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).build());

        mockMvc.perform(
                        post("/items")
                                .headers(getHeaders())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(createItemDto))
                )
                .andExpect(status().isCreated());

        verify(itemClient).createItem(any(CreateItemDto.class), eq(USER_ID));
    }

    @Test
    void shouldReturnStatus200_whenItemUpdated() throws Exception {

        UpdateItemDto updateItemDto = new UpdateItemDto();

        when(itemClient.updateItem(any(UpdateItemDto.class), anyLong(), anyLong()))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(
                        patch("/items/{itemId}", itemId)
                                .content(objectMapper.writeValueAsString(updateItemDto))
                                .contentType(MediaType.APPLICATION_JSON)
                                .headers(getHeaders())
                                .accept(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8)
                )
                .andExpect(status().isOk());

        verify(itemClient, times(1)).updateItem(
                any(UpdateItemDto.class), eq(USER_ID), eq(itemId));
    }

    @Test
    void shouldReturnBadRequest_whenItemIdIsNegative() throws Exception {

        UpdateItemDto updateItemDto = new UpdateItemDto();
        long itemId = -1L;

        when(itemClient.updateItem(any(UpdateItemDto.class), anyLong(), anyLong()))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(
                        patch("/items/{itemId}", itemId)
                                .content(objectMapper.writeValueAsString(updateItemDto))
                                .contentType(MediaType.APPLICATION_JSON)
                                .headers(getHeaders())
                                .accept(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Ошибка валидации"));

        verifyNoInteractions(itemClient);
    }

    @Test
    void shouldReturnStatus200_whenExistItemById() throws Exception {

        when(itemClient.getItemById(anyLong()))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(
                        get("/items/{itemId}", itemId)
                                .characterEncoding(StandardCharsets.UTF_8)
                                .headers(getHeaders())
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        verify(itemClient).getItemById(itemId);
    }

    @Test
    void shouldReturnStatus201AndComment_whenCreatedCommentToItem() throws Exception {

        CreateCommentDto createCommentDto = new CreateCommentDto();
        createCommentDto.setText("Custom text");

        when(itemClient.addComment(anyLong(), anyLong(), any(CreateCommentDto.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).build());

        mockMvc.perform(
                        post("/items/{itemId}/comment", itemId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(createCommentDto))
                                .headers(getHeaders())
                )
                .andExpect(status().isCreated());

        verify(itemClient).addComment(anyLong(), anyLong(), any(CreateCommentDto.class));
    }

    @Test
    void shouldReturnBadRequest_whenCommentTextIsBlankOrMissing() throws Exception {

        CreateCommentDto createCommentDto = new CreateCommentDto();

        when(itemClient.addComment(anyLong(), anyLong(), any(CreateCommentDto.class)))
                .thenThrow(HandlerMethodValidationException.class);

        mockMvc.perform(
                        post("/items/{itemId}/comment", itemId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(createCommentDto))
                                .headers(getHeaders())
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(itemClient);
    }

    @Test
    void shouldReturnStatus200AndItemsList_whenSearchByItemTextOrDescription() throws Exception {

        String text = "description";

        List<Object> itemDtoList = List.of("Item description 1", "Item description 3", "Item description 5");

        when(itemClient.searchItemsByName(anyLong(), anyString()))
                .thenReturn(ResponseEntity.ok().body(itemDtoList));

        mockMvc.perform(
                        get("/items/search?text={text}", text)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(itemDtoList))
                                .headers(getHeaders())
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        verify(itemClient).searchItemsByName(eq(USER_ID), eq(text));
    }

    @Test
    void shouldItems_whenFindByUserId() throws Exception {
        when(itemClient.findItemsWithAfterAndBeforeBookingDateByUserId(anyLong()))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(
                get("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .headers(getHeaders())
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

        verify(itemClient).findItemsWithAfterAndBeforeBookingDateByUserId(eq(USER_ID));
    }
}