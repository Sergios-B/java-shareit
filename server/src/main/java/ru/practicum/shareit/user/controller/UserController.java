package ru.practicum.shareit.user.controller;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserDto> getUsers() {
        return userService.findAllUsers();
    }

    @GetMapping("/{userid}")
    public UserDto getUser(@PathVariable Long userid) {
        return userService.findUserById(userid);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@RequestBody CreateUserDto userDto) {
        return userService.createUser(userDto);
    }

    @DeleteMapping("/{userid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long userid) {
        userService.deleteUser(userid);
    }

    @PatchMapping("/{userid}")
    public UserDto updateUser(@PathVariable Long userid, @RequestBody UpdateUserDto userDto) {
        return userService.updateUser(userDto, userid);
    }
}
