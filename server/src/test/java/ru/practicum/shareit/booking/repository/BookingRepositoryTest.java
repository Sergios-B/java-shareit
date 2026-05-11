package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.shareit.CommonRepositoryTestInit;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BookingRepositoryTest extends CommonRepositoryTestInit {
    @Autowired
    private BookingRepository bookingRepository;

    private User booker;
    private Item item;
    private final LocalDateTime now = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        booker = users.getFirst();
        User owner = users.get(1);
        item = itemRepository.save(Item.builder()
                .name(generatorText(10))
                .description(generatorText(30))
                .available(true)
                .owner(owner).build());
    }

    @Test
    void existsByItemIdAndBookerIdAndStatusAndEndBefore_shouldReturnTrueWhenFinished() {
        saveBookings(
                Booking.builder().start(now.minusDays(2)).end(now.minusDays(1)).item(item).booker(booker)
                        .status(BookingStatus.APPROVED).build()
        );

        boolean exists = bookingRepository
                .existsByItemIdAndBookerIdAndStatusAndEndBefore(item.getId(), booker.getId(), BookingStatus.APPROVED, now);

        assertThat(exists).isTrue();
    }

    @Test
    void existsByItemIdAndStartLessThanAndEndGreaterThan_shouldFindOverlapping() {
        saveBookings(
                Booking.builder().start(now.withHour(10)).end(now.withHour(12)).item(item).booker(booker)
                        .status(BookingStatus.APPROVED).build()
        );

        boolean overlaps = bookingRepository
                .existsByItemIdAndStartLessThanAndEndGreaterThan(item.getId(), now.withHour(13), now.withHour(11));

        assertThat(overlaps).isTrue();
    }

    @Test
    void findAllByStatusNotAndStartLessThanAndItemIdIn_shouldFindLastBookings() {

        saveBookings(
                Booking.builder().start(now.minusDays(1)).end(now.minusMinutes(30)).item(item).booker(booker)
                        .status(BookingStatus.APPROVED).build()
        );

        List<Booking> result = bookingRepository.findAllByStatusNotAndStartLessThanAndItemIdInOrderByStartDesc(
                BookingStatus.REJECTED, now, List.of(item.getId()));

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getStatus()).isNotEqualTo(BookingStatus.REJECTED);
    }

    private void saveBookings(Booking... bookings) {
        bookingRepository.saveAll(Arrays.asList(bookings));
    }
}