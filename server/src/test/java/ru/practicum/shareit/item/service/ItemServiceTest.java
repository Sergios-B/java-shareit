package ru.practicum.shareit.item.service;

import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.exception.NotUserPermissionException;
import ru.practicum.shareit.item.dto.item.CreateItemDto;
import ru.practicum.shareit.item.dto.item.ItemDto;
import ru.practicum.shareit.item.dto.item.UpdateItemDto;
import ru.practicum.shareit.item.exeption.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.impl.ItemBookingServiceImpl;
import ru.practicum.shareit.item.service.impl.ItemServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserServiceImpl userService;

    @InjectMocks
    private ItemServiceImpl itemService;

    @InjectMocks
    private ItemBookingServiceImpl itemBookingService;

    private User owner;

    @BeforeEach
    public void setUp() {
        owner = User.builder()
                .id(1L)
                .email("example.yandex.com")
                .name("Albert")
                .build();
    }

    @Test
    public void should_returnException_whenItemDoesNotExist() {

        Long expectedId = 1L;

        when(itemRepository.findById(expectedId)).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class,
                () -> itemBookingService.getItemById(expectedId));
    }

    @Test
    public void should_returnUserNotFoundException_whenUserIdNotExistsWhenUpdateItem() {
        Long expectedId = 1L;
        Long userId = 2L;
        Long ownerId = 3L;

        UpdateItemDto itemDto = new UpdateItemDto();

        User owner = User.builder().id(ownerId).build();

        Item item = Item.builder().id(expectedId).owner(owner).build();

        when(itemRepository.findById(expectedId)).thenReturn(Optional.of(item));

        assertThrows(NotUserPermissionException.class, () -> itemService.updateItem(itemDto, userId, expectedId));

        verify(itemRepository).findById(expectedId);

        verify(itemRepository, never()).save(any());
    }

    @Test
    public void should_returnItemNotFoundException_whenItemIdNotExistsWhenUpdateItem() {
        Long expectedId = 1L;
        Long userId = 2L;

        UpdateItemDto itemDto = new UpdateItemDto();
        itemDto.setName("Item name");
        itemDto.setDescription("Item description");
        itemDto.setAvailable(true);

        when(itemRepository.findById(expectedId)).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class,
                () -> itemService.updateItem(itemDto, userId, expectedId));

        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    public void should_returnNewItem_whenParamsAreValid() {
        Long expectedId = 1L;
        Long expectedUserId = 1L;

        String expectedName = "New item name";
        String expectedDescription = "New item description";

        UpdateItemDto updateDto = new UpdateItemDto();
        updateDto.setAvailable(true);
        updateDto.setName("Item name");
        updateDto.setDescription("Item description");

        Item item = Item.builder()
                .id(expectedId)
                .name(expectedName)
                .description(expectedDescription)
                .available(true)
                .owner(owner)
                .build();

        when(itemRepository.findById(expectedId)).thenReturn(Optional.of(item));

        when(itemRepository.save(item)).thenReturn(item);

        ItemDto newItem = itemService.updateItem(updateDto, expectedUserId, expectedId);

        AssertionsForClassTypes.assertThat(newItem)
                .hasFieldOrPropertyWithValue("id", expectedId)
                .hasFieldOrPropertyWithValue("name", newItem.getName())
                .hasFieldOrPropertyWithValue("description", newItem.getDescription());
    }

    @Test
    public void should_returnCreatedItem_whenParamsAreValid() {
        Long expectedId = 1L;
        Long expectedUserId = 2L;

        String expectedName = "New item name";
        String expectedDescription = "New item description";

        CreateItemDto createItem = new CreateItemDto();
        createItem.setName(expectedName);
        createItem.setDescription(expectedDescription);
        createItem.setAvailable(true);

        Item newItem = Item.builder()
                .id(expectedId)
                .name(expectedName)
                .description(expectedDescription)
                .available(true)
                .owner(owner)
                .build();

        when(userService.findUserEntityByIdOrThrowAnException(expectedUserId)).thenReturn(new User());
        when(itemRepository.save(any(Item.class))).thenReturn(newItem);

        ItemDto item = itemService.createItem(createItem, expectedUserId);

        AssertionsForClassTypes.assertThat(item)
                .hasFieldOrPropertyWithValue("id", expectedId)
                .hasFieldOrPropertyWithValue("name", newItem.getName())
                .hasFieldOrPropertyWithValue("description", newItem.getDescription());
    }

    @Test
    public void should_returnEmptyList_whenNameIsEmpty() {
        String name = "";

        List<ItemDto> itemList = itemService.searchItemsByName(name);

        Assertions.assertTrue(itemList.isEmpty());

        verifyNoInteractions(itemRepository);
    }

    @Test
    public void should_returnItems_whenNameIsValid() {
        String searchName = "world";
        Item item = Item.builder()
                .id(1L)
                .name("Hello world")
                .description("How are you")
                .available(true)
                .owner(owner)
                .build();

        when(itemRepository.findAllByNameIsLikeOrDescriptionIsLike(searchName)).thenReturn(List.of(item));

        List<ItemDto> itemList = itemService.searchItemsByName(searchName);

        Assertions.assertFalse(itemList.isEmpty());
        Assertions.assertEquals(1, itemList.size());

        verify(itemRepository).findAllByNameIsLikeOrDescriptionIsLike(searchName);
    }
}
