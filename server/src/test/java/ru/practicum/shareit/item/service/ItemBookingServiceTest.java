package ru.practicum.shareit.item.service;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.item.ItemToOwnerDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.impl.ItemBookingServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class ItemBookingServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserServiceImpl userService;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentService commentService;

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
    void should_returnItem_whenExists() {

        Long expectedId = 1L;

        Item item = Item.builder()
                .id(expectedId)
                .name("Item title")
                .description("Item description")
                .available(true)
                .owner(owner)
                .build();

        when(itemRepository.findById(expectedId)).thenReturn(Optional.of(item));

        ItemToOwnerDto finedItem = itemBookingService.getItemById(expectedId);

        AssertionsForClassTypes.assertThat(finedItem)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", expectedId)
                .hasFieldOrPropertyWithValue("name", item.getName());

        verify(itemRepository).findById(expectedId);
    }

    @Test
    void should_returnListItem_whenFindItemsWithAfterAndBeforeBookingDateByUserId() {

        int expectedCount = 3;

        List<Item> items = List.of(
                Item.builder().id(1L).owner(owner).available(true).build(),
                Item.builder().id(2L).owner(owner).available(true).build(),
                Item.builder().id(3L).owner(owner).available(true).build()
        );
        List<Booking> firstBookings = List.of(
                Booking.builder().id(1L).booker(owner).item(items.getFirst()).build(),
                Booking.builder().id(2L).booker(owner).item(items.getLast()).build()
        );
        List<Booking> lastBookings = List.of(
                Booking.builder().id(3L).booker(owner).item(items.getLast()).build()
        );
        List<Comment> comments = List.of(
                Comment.builder().id(1L).item(items.getLast()).author(owner).build(),
                Comment.builder().id(2L).item(items.getFirst()).author(owner).build()
        );

        when(userService.findUserEntityByIdOrThrowAnException(owner.getId())).thenReturn(owner);
        when(itemRepository.findAllByOwnerId(owner.getId())).thenReturn(items);
        when(bookingRepository
                .findAllByStatusNotAndStartLessThanAndItemIdInOrderByStartDesc(any(BookingStatus.class), any(), anyList()))
                .thenReturn(firstBookings);
        when(bookingRepository
                .findAllByStatusNotAndStartGreaterThanAndItemIdInOrderByStartAsc(any(BookingStatus.class), any(), anyList()))
                .thenReturn(lastBookings);
        when(commentService.findCommentsByItemIds(anyList())).thenReturn(comments);

        List<ItemToOwnerDto> itemsByUserId = itemBookingService
                .findItemsWithAfterAndBeforeBookingDateByUserId(owner.getId());

        Assertions
                .assertThat(itemsByUserId)
                .isNotNull()
                .hasSize(expectedCount);
    }
}
