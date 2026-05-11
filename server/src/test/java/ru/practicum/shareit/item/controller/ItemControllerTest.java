package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.BaseUnitTest;
import ru.practicum.shareit.MockGeneratorTest;
import ru.practicum.shareit.item.dto.comment.CommentDto;
import ru.practicum.shareit.item.dto.comment.CreateCommentDto;
import ru.practicum.shareit.item.dto.item.CreateItemDto;
import ru.practicum.shareit.item.dto.item.ItemDto;
import ru.practicum.shareit.item.dto.item.ItemToOwnerDto;
import ru.practicum.shareit.item.dto.item.UpdateItemDto;
import ru.practicum.shareit.item.service.CommentService;
import ru.practicum.shareit.item.service.ItemBookingService;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest extends BaseUnitTest {

    @MockBean
    private ItemService itemService;

    @MockBean
    private ItemBookingService itemBookingService;

    @MockBean
    private CommentService commentService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final String mockName = MockGeneratorTest.generatorText(10);

    private final String mockDescription = MockGeneratorTest.generatorText(30);

    private final long mockItemId = 1L;

    private ItemDto itemDto;

    @BeforeEach
    void setUp() {
        itemDto = ItemDto.builder()
                .id(mockItemId)
                .name(mockName)
                .description(mockDescription)
                .build();
    }

    @Test
    void shouldReturnItem_whenCreated() throws Exception {

        CreateItemDto createItemDto = new CreateItemDto();

        ItemDto itemDto = ItemDto.builder()
                .name(mockName)
                .description(mockDescription)
                .build();

        when(itemService.createItem(any(CreateItemDto.class), anyLong()))
                .thenReturn(itemDto);

        mockMvc.perform(
                        post("/items")
                                .content(objectMapper.writeValueAsString(createItemDto))
                                .contentType(MediaType.APPLICATION_JSON)
                                .headers(getHeaders())
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(mockName))
                .andExpect(jsonPath("$.description").value(mockDescription));

        verify(itemService).createItem(any(CreateItemDto.class), eq(USER_ID));
    }

    @Test
    void shouldReturnItem_whenUpdated() throws Exception {

        UpdateItemDto updateItemDto = new UpdateItemDto();

        when(itemService.updateItem(any(UpdateItemDto.class), anyLong(), anyLong())).thenReturn(itemDto);

        mockMvc.perform(
                        patch("/items/{itemId}", mockItemId)
                                .headers(getHeaders())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateItemDto))
                                .characterEncoding(StandardCharsets.UTF_8)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(mockName))
                .andExpect(jsonPath("$.description").value(mockDescription));

        verify(itemService).updateItem(any(UpdateItemDto.class), eq(USER_ID), eq(mockItemId));
    }

    @Test
    void shouldReturnItem_whenFindById() throws Exception {

        ItemToOwnerDto item = ItemToOwnerDto.builder()
                .id(mockItemId)
                .name(mockName)
                .description(mockDescription)
                .build();

        when(itemBookingService.getItemById(anyLong())).thenReturn(item);

        mockMvc.perform(
                get("/items/{itemId}", mockItemId)
                        .headers(getHeaders())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

        verify(itemBookingService).getItemById(eq(mockItemId));
    }

    @Test
    void shouldReturnItems_whenSearchActualByNameOrDescription() throws Exception {

        String text = "молот";

        List<ItemDto> items = List.of(
                ItemDto.builder().name("Молоток").build(),
                ItemDto.builder().name("Гвозди с молотоком").build()
        );

        when(itemService.searchItemsByName(anyString())).thenReturn(items);

        mockMvc.perform(
                        get("/items/search")
                                .param("text", text)
                                .contentType(MediaType.APPLICATION_JSON)
                                .headers(getHeaders())
                                .characterEncoding(StandardCharsets.UTF_8)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(items.size()))
                .andExpect(jsonPath("$[0].name").value(items.getFirst().getName()))
                .andExpect(jsonPath("$[1].name").value(items.getLast().getName()));

        verify(itemService).searchItemsByName(eq(text));
    }

    @Test
    void shouldReturnComment_whenCreatedComment() throws Exception {

        String expectedText = "Comment 1";

        CreateCommentDto createCommentDto = new CreateCommentDto();

        CommentDto commentDto = CommentDto.builder().text(expectedText).build();

        when(commentService.addComment(anyLong(), anyLong(), any(CreateCommentDto.class)))
                .thenReturn(commentDto);

        mockMvc.perform(
                        post("/items/{itemId}/comment", mockItemId)
                                .characterEncoding(StandardCharsets.UTF_8)
                                .headers(getHeaders())
                                .content(objectMapper.writeValueAsString(createCommentDto))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.text").value(expectedText));

        verify(commentService).addComment(eq(mockItemId), eq(USER_ID), any(CreateCommentDto.class));
    }

    @Test
    void shouldReturnItems_whenFindByOwnerId() throws Exception {

        List<ItemToOwnerDto> items = List.of(
                ItemToOwnerDto.builder().id(1L).build(),
                ItemToOwnerDto.builder().id(2L).build()
        );

        when(itemBookingService.findItemsWithAfterAndBeforeBookingDateByUserId(USER_ID))
                .thenReturn(items);

        mockMvc.perform(
                        get("/items", mockItemId)
                                .headers(getHeaders())
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(items.size()))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L));

        verify(itemBookingService).findItemsWithAfterAndBeforeBookingDateByUserId(USER_ID);
    }
}