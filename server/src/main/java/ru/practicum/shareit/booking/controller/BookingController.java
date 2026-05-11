package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDto saveBooking(
             @RequestBody CreateBookingDto bookingDto,
             @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        return bookingService.createBooking(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateApproveBooking(
            @PathVariable("bookingId") Long id,
            @RequestParam(defaultValue = "false") boolean approved,
            @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        return bookingService.updateBookingApproved(id, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(
            @PathVariable("bookingId") Long id,
            @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        return bookingService.findBookingById(id, userId);
    }

    @GetMapping
    public List<BookingDto> getBookingsByUserId(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "ALL") BookingState state
    ) {
        return bookingService.findBookingByState(userId, state, false);
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingsByOwner(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "ALL") BookingState state
    ) {
        return bookingService.findBookingByState(userId, state, true);
    }
}
