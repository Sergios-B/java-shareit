package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateBookingDto {
    @NotNull(message = "Дата начала бронирование обязательна к заполнению")
    @FutureOrPresent(message = "Дата окончание бронирование должна быть в будующем времени или сейчас")
    private LocalDateTime start;

    @NotNull(message = "Дата окончание бронирование обязательна к заполнению")
    @Future(message = "Дата окончание бронирование должна быть в будующем времени")
    private LocalDateTime end;

    @NotNull(message = "Id вещи не указанно")
    @Positive(message = "Id вещи не должно быть меньше 0")
    private Long itemId;
}