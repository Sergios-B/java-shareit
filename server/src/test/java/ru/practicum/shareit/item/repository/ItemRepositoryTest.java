package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.CommonRepositoryTestInit;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemShortData;

import java.util.List;
import java.util.Objects;

class ItemRepositoryTest extends CommonRepositoryTestInit {

    @Test
    void shouldReturnListItems_whenFindByNameOrDescription() {

        String searchName = "er";

        int expectedSize = (int) items.stream()
                .filter(item -> item.getName().toLowerCase().contains(searchName) ||
                        item.getDescription().toLowerCase().contains(searchName))
                .count();

        List<Item> searchItems = itemRepository.findAllByNameIsLikeOrDescriptionIsLike(searchName);

        Assertions.assertEquals(expectedSize, searchItems.size(),
                "Должен был вернуться один так как второй уже занят");
    }

    @Test
    void shouldReturnListOfItems_whenFindByOwnerId() {

        Long userId = users.getFirst().getId();

        int expectedSize = (int) items.stream().filter(item -> Objects.equals(item.getOwner().getId(), userId)).count();

        List<Item> searchItems = itemRepository.findAllByOwnerId(userId);

        Assertions.assertEquals(expectedSize, searchItems.size(),
                "Должно было вернутся " + expectedSize + " записи по id владельца");
    }

    @Test
    void shouldReturnListOfItems_whenFindByRequestIds() {

        int expectedSize = 2;

        List<Item> newItems =
                List.of(
                        generateItem(generatorText(10), generatorText(30), randomItem().getId()),
                        generateItem(generatorText(10), generatorText(30), randomItem().getId())
                );

        List<Long> ids = newItems.stream().map(Item::getRequestId).toList();

        List<ItemShortData> searchItems = itemRepository.findAllByRequestIdIn(ids);

        Assertions.assertEquals(expectedSize, searchItems.size());
    }
}