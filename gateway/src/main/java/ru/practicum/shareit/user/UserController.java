package ru.practicum.shareit.user;


import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserClient userClient;

    @GetMapping
    public Object getUsers() {
        return userClient.findAll();
    }

    @GetMapping("/{userid}")
    public Object getUser(
            @Positive(message = "Id должно быть больше 0") @PathVariable Long userid
    ) {
        return userClient.findById(userid);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Object createUser(
            @Valid @RequestBody CreateUserDto userDto
    ) {
        return userClient.create(userDto);
    }

    @DeleteMapping("/{userid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(
            @Positive(message = "Id должно быть больше 0") @PathVariable Long userid
    ) {
        userClient.deleteUser(userid);
    }

    @PatchMapping("/{userid}")
    public Object updateUser(
            @Positive(message = "Id должно быть больше 0") @PathVariable Long userid,
            @Valid @RequestBody UpdateUserDto userDto
    ) {
        return userClient.update(userDto, userid);
    }
}
