package ru.practicum.shareit.user.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

class CreateUserDtoTest {

    private Validator validator;

    private CreateUserDto createUserDto;

    @BeforeEach
    public void setUp() {

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }

        createUserDto = new CreateUserDto();
        createUserDto.setEmail("example@mail.ru");
        createUserDto.setName("TestName");
    }

    @Test
    public void test_email_validate_field() {

        createUserDto.setEmail(null);

        Set<ConstraintViolation<CreateUserDto>> violations = validator.validate(createUserDto);

        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Поле 'email' не должно быть пустым")));

        createUserDto.setEmail("");

        violations = validator.validate(createUserDto);

        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Поле 'email' не должно быть пустым")));

        createUserDto.setEmail("test");

        violations = validator.validate(createUserDto);

        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Не валидный email")));

        createUserDto.setEmail("example@mail.ru");

        violations = validator.validate(createUserDto);

        assertTrue(violations.isEmpty(), "Ошибок не должно быть");
    }

    @Test
    public void test_name_validate_field() {
        createUserDto.setName(null);

        Set<ConstraintViolation<CreateUserDto>> violations = validator.validate(createUserDto);

        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Поле 'name' не должно быть пустым")));

        createUserDto.setName("TestName");

        violations = validator.validate(createUserDto);

        assertTrue(violations.isEmpty(), "Ошибок не должно быть");
    }
}