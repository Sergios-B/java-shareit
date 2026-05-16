package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.exception.BookingNotFoundException;
import ru.practicum.shareit.booking.exception.NotAvailableItemException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.InvalidBookingDateException;
import ru.practicum.shareit.exception.NotUserPermissionException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Mock
    private UserService userService;

    @Mock
    private ItemService itemService;

    private CreateBookingDto createBookingDto;

    private User user;

    private Item item;

    @BeforeEach
    void setUp() {
        createBookingDto = new CreateBookingDto();

        createBookingDto.setItemId(1L);

        user = User.builder().id(1L).email("example@mail.ru").name("Petia").build();

        item = Item.builder().available(true).id(1L).owner(user).build();
    }

    @Test
    void should_returnException_whenBookingStartDateBeforeEndDate() {

        createBookingDto.setStart(LocalDateTime.now());
        createBookingDto.setEnd(LocalDateTime.now().minusHours(1));

        assertThrows(InvalidBookingDateException.class,
                () -> bookingService.createBooking(createBookingDto, 1L));
    }

    @Test
    void should_returnException_whenItemIsFreeForBookingDate() {
        createBookingDto.setStart(LocalDateTime.now());
        createBookingDto.setEnd(LocalDateTime.now().plusDays(5));

        item.setAvailable(false);

        when(userService.findUserEntityByIdOrThrowAnException(1L)).thenReturn(user);
        when(itemService.findItemEntityByIdOrThrowOnException(createBookingDto.getItemId())).thenReturn(item);

        assertThrows(NotAvailableItemException.class,
                () -> bookingService.createBooking(createBookingDto, 1L));

        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void should_returnBooking_whenSuccessfulCreatedBooking() {

        createBookingDto.setStart(LocalDateTime.now());
        createBookingDto.setEnd(LocalDateTime.now().plusDays(5));
        createBookingDto.setItemId(item.getId());

        Booking booking = Booking.builder().id(1L)
                .start(createBookingDto.getStart())
                .end(createBookingDto.getEnd())
                .booker(user)
                .item(item)
                .build();

        when(userService.findUserEntityByIdOrThrowAnException(anyLong())).thenReturn(user);
        when(itemService.findItemEntityByIdOrThrowOnException(anyLong())).thenReturn(item);
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingDto newBooking = bookingService.createBooking(createBookingDto, user.getId());

        assertThat(newBooking)
                .isNotNull()
                .hasFieldOrPropertyWithValue("start", createBookingDto.getStart())
                .hasFieldOrPropertyWithValue("end", createBookingDto.getEnd());
    }

    @Test
    void should_returnException_whenItemOwnerIdAndUserIdNotEqualsForUpdateBookingApproved() {

        Booking booking = Booking.builder().id(1L)
                .start(createBookingDto.getStart())
                .end(createBookingDto.getEnd())
                .booker(user)
                .item(item)
                .build();

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(itemService.findItemEntityByIdOrThrowOnException(anyLong())).thenReturn(item);

        assertThrows(NotUserPermissionException.class,
                () -> bookingService.updateBookingApproved(1L, true, 2L));

        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void should_returnBooking_whenSuccessfulUpdatedBooking() {

        LocalDateTime expectedStart = createBookingDto.getStart();
        LocalDateTime expectedEnd = createBookingDto.getEnd();

        Booking booking = Booking.builder().id(1L)
                .start(expectedStart)
                .end(expectedEnd)
                .booker(user)
                .item(item)
                .build();

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(itemService.findItemEntityByIdOrThrowOnException(anyLong())).thenReturn(item);
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingDto updatedBooking = bookingService.updateBookingApproved(1L, true, user.getId());

        assertThat(updatedBooking)
                .isNotNull()
                .hasFieldOrPropertyWithValue("start", expectedStart)
                .hasFieldOrPropertyWithValue("end", expectedEnd)
                .hasFieldOrPropertyWithValue("booker.id", user.getId());

        verify(bookingRepository).save(booking);
    }

    @Test
    void should_returnException_whenBookingNotFoundById() {

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(BookingNotFoundException.class,
                () -> bookingService.findBookingById(1L, 1L));
    }

    @Test
    void should_returnException_whenIsNotTheItemOwnerIdAndBookingBrokerId() {

        Booking booking = Booking.builder().id(1L).booker(user).item(item).build();

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.ofNullable(booking));

        assertThrows(NotUserPermissionException.class,
                () -> bookingService.findBookingById(1L, 2L));
    }

    @Test
    void should_returnBookingInfo_whenIsTheItemOwnerIdOrBookingBrokerId() {

        Long expectedBookingId = 1L;
        Long expectedUserId = 1L;

        Booking booking = Booking.builder().id(expectedBookingId).booker(user).item(item).build();

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.ofNullable(booking));

        BookingDto findBooking = bookingService.findBookingById(expectedBookingId, expectedUserId);

        assertThat(findBooking)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", expectedBookingId)
                .hasFieldOrPropertyWithValue("item.id", item.getId())
                .hasFieldOrPropertyWithValue("booker.id", expectedUserId);

        verify(bookingRepository).findById(expectedBookingId);
    }

    @Test
    void should_returnList_whenFindBookingByStateByOwnerFalse() {

        Booking booking = Booking.builder().id(1L).booker(user).item(item).build();

        Long userId = 1L;

        when(bookingRepository.findAll(any(Specification.class), any(Sort.class))).thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.findBookingByState(userId, BookingState.ALL, false);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(booking.getId(), result.getFirst().getId());

        verify(bookingRepository).findAll(any(Specification.class), any(Sort.class));
        verify(userService).findUserEntityByIdOrThrowAnException(userId);
    }

    @Test
    void should_returnEmptyList_whenFindBookingByStateIsFuture() {

        Booking booking = Booking.builder().id(1L).booker(user).item(item).build();

        when(bookingRepository.findAll(any(Specification.class), any(Sort.class))).thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.findBookingByState(1L, BookingState.FUTURE, true);

        assertFalse(result.isEmpty());
    }

    @Test
    void should_returnList_whenExistsBookingByUseridAndItemId() {

        when(bookingRepository
                .existsByItemIdAndBookerIdAndStatusAndEndBefore(
                        anyLong(), anyLong(), any(BookingStatus.class), any(LocalDateTime.class)))
                .thenReturn(true);

        assertTrue(bookingService.existsBookingByUserIdAndItemId(1L, 1L));

        verify(bookingRepository).existsByItemIdAndBookerIdAndStatusAndEndBefore(
                anyLong(), anyLong(), any(BookingStatus.class), any(LocalDateTime.class));
    }
}