package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.MockGeneratorTest;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    private final long mockUserId = 1L;

    @Test
    void shouldReturnStatus404_whenUserIdNotFound() throws Exception {

        String errorMessage = "Пользователь с таким id не найден";

        when(userService.findUserById(anyLong()))
                .thenThrow(new UserNotFoundException(errorMessage));

        mockMvc.perform(
                        get("/users/{userId}", mockUserId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(errorMessage));

        verify(userService).findUserById(mockUserId);
    }

    @Test
    void shouldReturnUser_whenUserCreated() throws Exception {

        String email = MockGeneratorTest.generatorEmail();
        String name = MockGeneratorTest.generatorText(10);

        CreateUserDto createUserDto = new CreateUserDto();
        createUserDto.setEmail(email);
        createUserDto.setName(name);

        UserDto user = UserDto.builder().name(name).email(email).build();

        when(userService.createUser(createUserDto)).thenReturn(user);

        mockMvc.perform(
                        post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(createUserDto))
                                .characterEncoding(StandardCharsets.UTF_8)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.email").value(email));

        verify(userService).createUser(any(CreateUserDto.class));
    }

    @Test
    void shouldReturnEmptyContent_whenUserDeleted() throws Exception {

        UserDto user = UserDto.builder().id(mockUserId).build();

        when(userService.findUserById(mockUserId)).thenReturn(user);

        mockMvc.perform(
                delete("/users/{userId}", mockUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
        ).andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUser(eq(mockUserId));
    }

    @Test
    void shouldReturnUser_whenUserUpdated() throws Exception {

        String name = MockGeneratorTest.generatorText(10);

        UpdateUserDto updateUserDto = new UpdateUserDto();

        UserDto user = UserDto.builder().id(mockUserId).name(name)
                .build();

        when(userService.updateUser(any(UpdateUserDto.class), anyLong())).thenReturn(user);

        mockMvc.perform(
                        patch("/users/{userId}", mockUserId)
                                .content(objectMapper.writeValueAsString(updateUserDto))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(mockUserId))
                .andExpect(jsonPath("$.name").value(name));

        verify(userService, times(1)).updateUser(any(UpdateUserDto.class), eq(mockUserId));
    }

    @Test
    void shouldReturnUser_whenUserFined() throws Exception {

        UserDto user = UserDto.builder().id(mockUserId).build();

        when(userService.findUserById(mockUserId)).thenReturn(user);

        mockMvc.perform(
                        get("/users/{userId}", mockUserId)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(mockUserId));
    }

    @Test
    void shouldReturnListUsers()  throws Exception {

        List<UserDto> users = List.of(
                UserDto.builder().id(1L).build(),
                UserDto.builder().id(2L).build()
        );

        when(userService.findAllUsers()).thenReturn(users);

        mockMvc.perform(
                        get("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(users.size()))
                .andExpect(jsonPath("$[0].id").value(1L));

        verify(userService).findAllUsers();
    }
}