package ru.practicum.shareit.booking.exception;

public class NotAvailableItemException extends RuntimeException {
    public NotAvailableItemException(String message) {
        super(message);
    }
}
