package ru.practicum.shareit.request.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;


class CreateItemRequestDtoTest {

    private Validator validator;

    private CreateItemRequestDto createItemRequestDto;

    @BeforeEach
    void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }

        createItemRequestDto = new CreateItemRequestDto();
    }

    @Test
    void test_description_validate_field() {

        String messageError = "Описания запроса не должен быть пустым";

        Set<ConstraintViolation<CreateItemRequestDto>> validation = validator.validate(createItemRequestDto);

        assertTrue(validation.stream()
                .anyMatch(v -> v.getMessage().equals(messageError)));

        createItemRequestDto.setDescription("");

        validation = validator.validate(createItemRequestDto);

        assertTrue(validation.stream()
                .anyMatch(v -> v.getMessage().equals(messageError)));

        createItemRequestDto.setDescription("Request description");

        validation = validator.validate(createItemRequestDto);

        assertTrue(validation.isEmpty());
    }
}