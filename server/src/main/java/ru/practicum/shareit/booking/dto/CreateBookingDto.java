package ru.practicum.shareit.booking.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CreateBookingDto {

    private LocalDateTime start;

    private LocalDateTime end;

    private Long itemId;
}
