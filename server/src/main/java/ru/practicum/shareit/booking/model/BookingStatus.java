package ru.practicum.shareit.booking.model;

public enum BookingStatus {
    WAITING("новое бронирование, ожидает одобрения"),
    APPROVED("бронирование подтверждено владельцем"),
    REJECTED("бронирование отклонено владельцем"),
    CANCELLED("бронирование отменено создателем");

    private final String description;

    BookingStatus(String description) {
        this.description = description;
    }

    public String getStatusDescription() {
        return description;
    }
}
