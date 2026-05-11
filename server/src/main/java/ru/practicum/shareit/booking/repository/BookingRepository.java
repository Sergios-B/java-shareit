package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long>, JpaSpecificationExecutor<Booking> {

    boolean existsByItemIdAndStartLessThanAndEndGreaterThan(Long itemId, LocalDateTime start, LocalDateTime end);

    List<Booking> findAllByStatusNotAndStartLessThanAndItemIdInOrderByStartDesc(
            BookingStatus status, LocalDateTime now, List<Long> itemIds);

    List<Booking> findAllByStatusNotAndStartGreaterThanAndItemIdInOrderByStartAsc(
            BookingStatus status, LocalDateTime now, List<Long> itemIds);

    boolean existsByItemIdAndBookerIdAndStatusAndEndBefore(Long itemId, Long bookerId, BookingStatus status, LocalDateTime endBefore);
}
