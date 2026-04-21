package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDto {
    private Long id;
    @NotBlank(groups = Create.class, message = "Имя не может быть пустым")
    private String name;
    @NotBlank(groups = Create.class, message = "Email не может быть пустым")
    @Email(groups = {Create.class, Update.class}, message = "Некорректный email")
    private String email;

    public interface Create {
    }

    public interface Update {
    }
}
