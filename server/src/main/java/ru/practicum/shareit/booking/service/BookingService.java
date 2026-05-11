package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.List;

public interface BookingService {
    BookingDto createBooking(CreateBookingDto bookingDto, Long userId);

    BookingDto updateBookingApproved(Long id, boolean approved, Long userId);

    BookingDto findBookingById(Long id, Long userId);

    List<BookingDto> findBookingByState(Long userId, BookingState state, boolean byOwner);

    boolean existsBookingByUserIdAndItemId(Long itemId, Long userId);
}
