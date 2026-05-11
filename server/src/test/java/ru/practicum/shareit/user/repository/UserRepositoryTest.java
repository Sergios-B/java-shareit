package ru.practicum.shareit.user.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.shareit.CommonRepositoryTestInit;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class UserRepositoryTest extends CommonRepositoryTestInit {

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldReturnUser_whenEmailExists() {

        String expectedEmail = users.getFirst().getEmail();

        Optional<User> user = userRepository.findByEmail(expectedEmail);

        assertThat(user)
                .isNotEmpty()
                .hasValueSatisfying(u -> assertThat(u.getEmail()).isEqualTo(expectedEmail));
    }
}