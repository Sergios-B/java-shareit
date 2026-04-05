package ru.practicum.shareit.user.service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final Map<Long, User> users = new HashMap<>();
    private long nextId = 1;

    @Override
    public Collection<UserDto> findAll() {
        return users.values().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto create(UserDto userDto) {
        validateEmailUnique(userDto.getEmail());
        User user = UserMapper.toUser(userDto);
        user.setId(nextId++);
        users.put(user.getId(), user);
        log.info("Создан пользователь с id: {}", user.getId());
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto update(Long userId, UserDto userDto) {
        User user = users.get(userId);
        if (user == null) {
            throw new NotFoundException("Пользователь с указанным id не найден");
        }
        if (userDto.getEmail() != null && !userDto.getEmail().equals(user.getEmail())) {
            validateEmailUnique(userDto.getEmail());
            user.setEmail(userDto.getEmail());
        }
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        log.info("Обновлен пользователь с id: {}", userId);
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto findById(Long userId) {
        User user = users.get(userId);
        if (user == null) {
            throw new RuntimeException("Пользователь не найден");
        }
        return UserMapper.toUserDto(user);
    }

    @Override
    public void delete(Long userId) {
        users.remove(userId);
        log.info("Удален пользователь с id: {}", userId);
    }

    private void validateEmailUnique(String email) {
        boolean exists = users.values().stream()
                .anyMatch(u -> u.getEmail().equalsIgnoreCase(email));
        if (exists) {
            throw new ConflictException("Пользователь с таким email уже существует");
        }
    }
}
