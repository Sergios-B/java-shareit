package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.MockGeneratorTest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@DataJpaTest
@Import(BookingServiceImpl.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class BookingServiceITTest {

    @Autowired
    private BookingRepository bookingRepository;

    @MockBean
    private ItemService itemService;

    @MockBean
    private UserService userService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private TestEntityManager entityManager;

    private final LocalDateTime now = LocalDateTime.now();

    @Test
    void shouldReturnCurrentBookings() {

        User user = User.builder().name(MockGeneratorTest.generatorText(10))
                .email(MockGeneratorTest.generatorEmail()).build();

        Item item = Item.builder().name(MockGeneratorTest.generatorText(10))
                .description(MockGeneratorTest.generatorText(30))
                .available(true)
                .owner(user)
                .build();

        Booking booking = Booking.builder()
                .start(now.minusHours(1))
                .end(now.plusHours(1))
                .status(BookingStatus.APPROVED)
                .item(item)
                .booker(user)
                .build();

        entityManager.persist(booking);

        List<BookingDto> result = bookingService.findBookingByState(user.getId(), BookingState.CURRENT, false);

        assertEquals(1, result.size());

        result = bookingService.findBookingByState(user.getId(), BookingState.WAITING, false);

        assertEquals(0, result.size());

        result = bookingService.findBookingByState(user.getId(), BookingState.REJECTED, false);

        assertEquals(0, result.size());

        result = bookingService.findBookingByState(user.getId(), BookingState.PAST, false);

        assertEquals(0, result.size());
    }
}
