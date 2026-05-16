package ru.practicum.shareit.item.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.item.CreateItemDto;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

class CreateItemDtoTest {

    private Validator validator;

    private CreateItemDto createItemDto;

    @BeforeEach
    public void setUp() {

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }

        createItemDto = new CreateItemDto();
        createItemDto.setName("TestName");
        createItemDto.setDescription("Test description");
        createItemDto.setAvailable(true);
    }

    @Test
    public void test_name_validate_field() {
        createItemDto.setName(null);

        Set<ConstraintViolation<CreateItemDto>> violations = validator.validate(createItemDto);

        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Поле 'name' не должно быть пустым")));

        createItemDto.setName("");

        violations = validator.validate(createItemDto);

        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Поле 'name' не должно быть пустым")));

        createItemDto.setName("TestName");

        violations = validator.validate(createItemDto);

        assertTrue(violations.isEmpty(), "Ошибок не должно быть");
    }

    @Test
    public void test_description_validate_field() {
        createItemDto.setDescription(null);

        Set<ConstraintViolation<CreateItemDto>> violations = validator.validate(createItemDto);

        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Поле 'description' не должно быть пустым")));

        createItemDto.setDescription("");

        violations = validator.validate(createItemDto);

        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Поле 'description' не должно быть пустым")));

        createItemDto.setDescription("Test description");

        violations = validator.validate(createItemDto);

        assertTrue(violations.isEmpty(), "Ошибок не должно быть");
    }

    @Test
    public void test_available_validate_field() {
        createItemDto.setAvailable(null);

        Set<ConstraintViolation<CreateItemDto>> violations = validator.validate(createItemDto);

        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Поле 'available' обязательное")));

        createItemDto.setAvailable(true);

        violations = validator.validate(createItemDto);

        assertTrue(violations.isEmpty(), "Ошибок не должно быть");
    }
}