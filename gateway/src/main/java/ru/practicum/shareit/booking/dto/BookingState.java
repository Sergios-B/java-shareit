package ru.practicum.shareit.booking.dto;

import lombok.Getter;

import java.util.Optional;
import java.util.stream.Stream;

@Getter
public enum BookingState {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static Optional<BookingState> from(String stringState) {
        return Stream.of(values())
                .filter(s -> s.name().equalsIgnoreCase(stringState))
                .findFirst();
    }
}
