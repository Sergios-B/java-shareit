package ru.practicum.shareit.request.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.shareit.CommonRepositoryTestInit;
import ru.practicum.shareit.request.model.ItemRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class RequestRepositoryTest extends CommonRepositoryTestInit {

    private List<ItemRequest> itemRequests;

    @Autowired
    private RequestRepository requestRepository;

    @BeforeEach
    void setUp() {
        itemRequests = requestRepository.saveAll(
                List.of(
                        ItemRequest.builder().description(generatorText(30)).requestorId(randomUser().getId())
                                .createdAt(LocalDateTime.now()).build(),
                        ItemRequest.builder().description(generatorText(30)).requestorId(randomUser().getId())
                                .createdAt(LocalDateTime.now()).build(),
                        ItemRequest.builder().description(generatorText(30)).requestorId(randomUser().getId())
                                .createdAt(LocalDateTime.now()).build()
                )
        );
    }

    @Test
    void shouldReturnItemRequests_whenFindByRequesterId() {

        long userId = randomUser().getId();

        int expectedSize = (int) itemRequests.stream().filter(r -> r.getRequestorId().equals(userId)).count();

        List<ItemRequest> requests = requestRepository.findItemRequestsByRequestorIdWithItems(userId);

        Assertions.assertEquals(expectedSize, requests.size());
    }

    @Test
    void shouldReturnItemRequest_whenFindById() {

        long expectedId = itemRequests.getFirst().getId();

        Optional<ItemRequest> request = requestRepository.findItemRequestByWithItems(expectedId);

        assertThat(request)
                .isNotNull()
                .hasValueSatisfying(request1 -> assertThat(request1.getId()).isEqualTo(expectedId));
    }
}