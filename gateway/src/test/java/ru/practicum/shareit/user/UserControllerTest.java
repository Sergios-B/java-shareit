package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.BaseUnitTest;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest extends BaseUnitTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    private UserClient userClient;

    @Test
    void shouldReturnStatus201_whenUserIsCreated() throws Exception {
        String name = "CustomName";
        String email = "example@mail.ru";

        CreateUserDto createUserDto = new CreateUserDto();
        createUserDto.setName(name);
        createUserDto.setEmail(email);

        when(userClient.create(any(CreateUserDto.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).build());

        mockMvc.perform(
                        post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(createUserDto))
                                .accept(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8)
                )
                .andExpect(status().isCreated());

        verify(userClient).create(any(CreateUserDto.class));
    }

    @Test
    void shouldReturnStatus204_whenUserIsDeleted() throws Exception {

        mockMvc.perform(
                        delete("/users/{userId}", USER_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8)
                )
                .andExpect(status().isNoContent());

        verify(userClient, times(1)).deleteUser(USER_ID);
    }

    @Test
    void shouldReturnStatus200_whenGetUsersList() throws Exception {

        when(userClient.findAll()).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(
                        get("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        verify(userClient).findAll();
    }

    @Test
    void shouldReturnStatus200_whenUserUpdated() throws Exception {

        UpdateUserDto updateUserDto = new UpdateUserDto();

        when(userClient.update(any(UpdateUserDto.class), anyLong()))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(
                        patch("/users/{userId}", USER_ID)
                                .content(objectMapper.writeValueAsString(updateUserDto))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        verify(userClient).update(any(UpdateUserDto.class), anyLong());
    }

    @Test
    void shouldReturnStatus404_whenUserIdIsNegative() throws Exception {

        UpdateUserDto updateUserDto = new UpdateUserDto();

        when(userClient.update(any(UpdateUserDto.class), anyLong())).thenThrow(IllegalArgumentException.class);

        mockMvc.perform(
                        patch("/users/", -1L)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound());

        Assertions.assertThrows(IllegalArgumentException.class, () -> userClient.update(updateUserDto, -1L));
    }
}