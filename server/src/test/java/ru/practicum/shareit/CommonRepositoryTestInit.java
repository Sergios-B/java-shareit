package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public abstract class CommonRepositoryTestInit {

    protected Random rnd = MockGeneratorTest.rnd;

    protected List<User> users;

    protected List<Item> items;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected ItemRepository itemRepository;

    @BeforeEach
    void init() {

        users = userRepository.saveAll(
                Stream.generate(() -> User.builder().name(generatorText(9)).email(generatorEmail()).build())
                        .limit(2).toList()
        );

        items = itemRepository.saveAll(
                Stream.generate(
                                () -> Item.builder()
                                        .name(generatorText(9))
                                        .description(generatorText(30))
                                        .available(true)
                                        .owner(users.get(rnd.nextInt(users.size())))
                                        .build()
                        )
                        .limit(4).toList()
        );
    }

    protected String generatorText(int length) {
        return MockGeneratorTest.generatorText(length);
    }

    protected String generatorEmail() {
        return MockGeneratorTest.generatorEmail();
    }

    protected Item randomItem() {
        return items.get(rnd.nextInt(items.size()));
    }

    protected User randomUser() {
        return users.get(rnd.nextInt(users.size()));
    }

    protected Item generateItem(String name, String description, Long requestId) {
        return itemRepository.save(
                Item.builder().name(name).description(description).requestId(requestId)
                        .owner(users.get(rnd.nextInt(users.size()))).build()
        );
    }
}
