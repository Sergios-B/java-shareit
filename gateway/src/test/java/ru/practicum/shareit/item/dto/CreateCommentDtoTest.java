package ru.practicum.shareit.item.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.comment.CreateCommentDto;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

class CreateCommentDtoTest {

    private Validator validator;

    private CreateCommentDto createCommentDto;

    @BeforeEach
    void setUp() {

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }

        createCommentDto = new CreateCommentDto();
    }

    @Test
    void test_description_validate_field() {

        String errorMessage = "Поле 'text' не должно быть пустым";

        Set<ConstraintViolation<CreateCommentDto>> validate = validator.validate(createCommentDto);

        assertTrue(validate.stream()
                .anyMatch(v -> v.getMessage().equals(errorMessage)));

        createCommentDto.setText("");

        validate = validator.validate(createCommentDto);

        assertTrue(validate.stream()
                .anyMatch(v -> v.getMessage().equals(errorMessage)));

        createCommentDto.setText("Comment description");

        validate = validator.validate(createCommentDto);

        assertTrue(validate.isEmpty());
    }
}