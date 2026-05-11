package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.booking.exception.BookingNotFoundException;
import ru.practicum.shareit.booking.exception.NotAvailableItemException;
import ru.practicum.shareit.item.exeption.ItemNotFoundException;
import ru.practicum.shareit.user.exception.EmailAlreadyUserException;
import ru.practicum.shareit.user.exception.UserNotFoundException;

import java.util.Map;

@RestControllerAdvice
public class ExceptionGlobalHandler {

    @ExceptionHandler(EmailAlreadyUserException.class)
    public ResponseEntity<ErrorResponse> handleEmailAlreadyUserException(RuntimeException e) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(e.getMessage(), Map.of()));
    }

    @ExceptionHandler({UserNotFoundException.class, ItemNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(RuntimeException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(e.getMessage(), Map.of()));
    }

    @ExceptionHandler({NotUserPermissionException.class})
    public ResponseEntity<ErrorResponse> handleUserNotUserPermissionException(RuntimeException e) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(e.getMessage(), Map.of()));
    }

    @ExceptionHandler(BookingNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(RuntimeException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(e.getMessage(), Map.of()));
    }

    @ExceptionHandler({NotAvailableItemException.class, InvalidBookingDateException.class})
    public ResponseEntity<ErrorResponse> handleUserAccessViolationException(RuntimeException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(e.getMessage(), Map.of()));
    }
}
