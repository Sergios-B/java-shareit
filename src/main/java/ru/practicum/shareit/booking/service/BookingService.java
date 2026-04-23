package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingIncomingDto;

import java.util.Collection;

public interface BookingService {

    BookingDto create(Long userId, BookingIncomingDto incomingDto);

    BookingDto updateStatus(Long userId, Long bookingId, Boolean approved);

    BookingDto findById(Long userId, Long bookingId);

    Collection<BookingDto> findAllByBooker(Long userId, String state);

    Collection<BookingDto> findAllByOwner(Long userId, String state);
}