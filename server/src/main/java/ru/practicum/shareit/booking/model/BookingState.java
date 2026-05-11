package ru.practicum.shareit.booking.model;

import lombok.Getter;

@Getter
public enum BookingState {
    ALL("All", "все"),
    CURRENT("CURRENT", "текущие"),
    PAST("PAST", "завершённые"),
    FUTURE("FUTURE", "будущие"),
    WAITING("WAITING", "ожидающие подтверждения"),
    REJECTED("REJECTED", "отклонённые");

    private final String name;

    private final String description;

    BookingState(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
