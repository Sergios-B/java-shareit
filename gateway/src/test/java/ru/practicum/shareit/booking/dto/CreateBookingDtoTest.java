package ru.practicum.shareit.booking.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

class CreateBookingDtoTest {

    private Validator validator;

    private CreateBookingDto createBookingDto;

    @BeforeEach
    void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }

        createBookingDto = new CreateBookingDto();

        createBookingDto.setStart(LocalDateTime.now().plusDays(1));
        createBookingDto.setEnd(LocalDateTime.now().plusDays(1));
        createBookingDto.setItemId(1L);
    }

    @Test
    void test_start_validate_field() {

        createBookingDto.setStart(null);

        Set<ConstraintViolation<CreateBookingDto>> validation = validator.validate(createBookingDto);

        assertTrue(validation.stream().anyMatch(v ->
                v.getMessage().equals("Дата начала бронирование обязательна к заполнению")));

        createBookingDto.setStart(LocalDateTime.now().minusDays(1));

        validation = validator.validate(createBookingDto);

        assertTrue(validation.stream().anyMatch(v ->
                v.getMessage().equals("Дата окончание бронирование должна быть в будующем времени или сейчас")));

        createBookingDto.setStart(LocalDateTime.now().plusDays(1));

        validation = validator.validate(createBookingDto);

        assertTrue(validation.isEmpty());
    }

    @Test
    void test_end_validate_field() {

        createBookingDto.setEnd(null);

        Set<ConstraintViolation<CreateBookingDto>> validation = validator.validate(createBookingDto);

        assertTrue(validation.stream().anyMatch(v ->
                v.getMessage().equals("Дата окончание бронирование обязательна к заполнению")));

        createBookingDto.setEnd(LocalDateTime.now().minusDays(1));

        validation = validator.validate(createBookingDto);

        assertTrue(validation.stream().anyMatch(v ->
                v.getMessage().equals("Дата окончание бронирование должна быть в будующем времени")));

        createBookingDto.setEnd(LocalDateTime.now().plusDays(1));

        validation = validator.validate(createBookingDto);

        assertTrue(validation.isEmpty());
    }

    @Test
    void test_itemId_validate_field() {

        createBookingDto.setItemId(null);

        Set<ConstraintViolation<CreateBookingDto>> validation = validator.validate(createBookingDto);

        assertTrue(validation.stream().anyMatch(v ->
                v.getMessage().equals("Id вещи не указанно")));

        createBookingDto.setItemId(-1L);

        validation = validator.validate(createBookingDto);

        assertTrue(validation.stream().anyMatch(v ->
                v.getMessage().equals("Id вещи не должно быть меньше 0")));

        createBookingDto.setItemId(5L);

        validation = validator.validate(createBookingDto);

        assertTrue(validation.isEmpty());
    }
}