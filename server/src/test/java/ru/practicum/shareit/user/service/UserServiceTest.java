package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.BaseUnitTest;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.EmailAlreadyUserException;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class UserServiceTest extends BaseUnitTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    @Test
    void should_returnUser_whenExists() {

        Long expectedId = 1L;

        User user = User.builder()
                .id(expectedId)
                .email("example@gmail.ru")
                .name("example name")
                .build();

        when(userRepository.findById(expectedId)).thenReturn(Optional.ofNullable(user));

        UserDto result = userService.findUserById(expectedId);

        assertNotNull(result);

        assertEquals("example name", result.getName());

        verify(userRepository, times(1)).findById(expectedId);
    }

    @Test
    void should_keepOldEmail_whenOnlyNameChanged() {
        Long expectedId = 1L;
        String expectedName = "New_name";
        String expectedEmail = "example@gmail.ru";

        User user = User.builder()
                .id(expectedId)
                .email(expectedEmail)
                .name("Old_name")
                .build();

        UpdateUserDto patchedUser = new UpdateUserDto();
        patchedUser.setName(expectedName);

        when(userRepository.findById(expectedId)).thenReturn(Optional.ofNullable(user));

        when(userRepository.save(any(User.class))).thenReturn(user);

        userService.updateUser(patchedUser, expectedId);

        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();

        assertThat(savedUser)
                .hasFieldOrPropertyWithValue("name", expectedName)
                .hasFieldOrPropertyWithValue("email", expectedEmail)
                .hasFieldOrPropertyWithValue("id", expectedId);
    }

    @Test
    void should_keepOldName_whenEmailChanged() {

        String expectedName = "Old_name";
        String expectedEmail = "new_example@gmail.ru";
        Long expectedId = 1L;

        User user = User.builder()
                .id(expectedId)
                .email(expectedEmail)
                .name(expectedName)
                .build();

        UpdateUserDto patchedUser = new UpdateUserDto();
        patchedUser.setEmail(expectedEmail);

        when(userRepository.findById(expectedId)).thenReturn(Optional.ofNullable(user));

        when(userRepository.findByEmail(any(String.class))).thenReturn(Optional.empty());

        when(userRepository.save(any(User.class))).thenReturn(user);

        userService.updateUser(patchedUser, expectedId);

        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();

        assertThat(savedUser)
                .hasFieldOrPropertyWithValue("name", expectedName)
                .hasFieldOrPropertyWithValue("email", expectedEmail)
                .hasFieldOrPropertyWithValue("id", expectedId);
    }

    @Test
    void should_returnUserNotFoundException_whenUserDoesNotExist() {
        Long expectedId = 100L;

        when(userRepository.findById(expectedId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(expectedId));

        verify(userRepository, never()).deleteById(expectedId);
    }

    @Test
    void should_deleteUser_whenUserExists() {
        Long expectedId = 1L;

        User user = User.builder()
                .id(expectedId)
                .email("Test@gmail.ru")
                .name("Test name")
                .build();

        when(userRepository.findById(expectedId)).thenReturn(Optional.ofNullable(user));

        userService.deleteUser(expectedId);

        verify(userRepository, times(1)).deleteById(expectedId);
    }

    @Test
    void should_createUser_whenEmailNoExists() {

        String email = "exampla@mail.ru";

        String name = "Test name";

        User user = User.builder().email(email).name(name).build();

        CreateUserDto createUser = new CreateUserDto();
        createUser.setEmail(email);
        createUser.setName(name);

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        when(userRepository.save(user)).thenReturn(user);

        UserDto userCreated = userService.createUser(createUser);

        assertThat(userCreated)
                .isNotNull()
                .hasFieldOrPropertyWithValue("email", email)
                .hasFieldOrPropertyWithValue("name", name);
    }

    @Test
    void should_createEmailAlreadyUserException_whenEmailExists() {

        String email = "exampla@mail.ru";

        String name = "Test name";

        User user = User.builder().email(email).name(name).build();

        CreateUserDto createUser = new CreateUserDto();
        createUser.setEmail(email);
        createUser.setName(name);

        when(userRepository.findByEmail(email)).thenReturn(Optional.ofNullable(user));

        assertThrows(EmailAlreadyUserException.class, () -> userService.createUser(createUser));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void should_updateEmailAlreadyUserException_whenEmailExists() {

        String email = "exampla@mail.ru";

        String name = "Test name";

        User user = User.builder().email(email).name(name).build();

        UpdateUserDto updateUser = new UpdateUserDto();
        updateUser.setEmail(email);

        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));

        when(userRepository.findByEmail(email)).thenReturn(Optional.ofNullable(user));

        assertThrows(EmailAlreadyUserException.class, () -> userService.updateUser(updateUser, USER_ID));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void should_returnAllUsers_whenUserExists() {

        List<User> users = List.of(
                User.builder().id(1L).build(),
                User.builder().id(2L).build(),
                User.builder().id(3L).build()
        );

        when(userRepository.findAll()).thenReturn(users);

        List<UserDto> findUsers = userService.findAllUsers();

        assertEquals(3, findUsers.size());
    }
}
