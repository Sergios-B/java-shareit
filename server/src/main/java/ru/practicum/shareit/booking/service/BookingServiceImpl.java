package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.exception.BookingNotFoundException;
import ru.practicum.shareit.booking.exception.NotAvailableItemException;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.InvalidBookingDateException;
import ru.practicum.shareit.exception.NotUserPermissionException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;

    private final ItemService itemService;

    private final UserService userService;

    @Override
    @Transactional
    public BookingDto createBooking(CreateBookingDto bookingDto, Long userId) {

        if (!bookingDto.getStart().isBefore(bookingDto.getEnd())) {
            throw new InvalidBookingDateException("Дата начало бронирование должно быть реньше чем дата окончание");
        }

        User user = userService.findUserEntityByIdOrThrowAnException(userId);

        Item item = itemService.findItemEntityByIdOrThrowOnException(bookingDto.getItemId());

        if (!item.isAvailable() || bookingRepository.existsByItemIdAndStartLessThanAndEndGreaterThan(
                bookingDto.getItemId(), bookingDto.getStart(), bookingDto.getEnd())) {
            log.info("[BookingServiceImpl.createBooking] вещь на этот срок уже занята");

            throw new NotAvailableItemException("Эта вещь пока занята");
        }

        Booking newBooking = BookingMapper.toBooking(bookingDto, item, user);

        newBooking = bookingRepository.save(newBooking);

        log.info("[BookingServiceImpl.createBooking] бронь успешно создана");

        return BookingMapper.toBookingDto(newBooking);
    }

    @Override
    @Transactional
    public BookingDto updateBookingApproved(Long id, boolean approved, Long userId) {

        Booking booking = findBookingEntityByIdOrThrowAnException(id);

        Item item = itemService.findItemEntityByIdOrThrowOnException(booking.getItem().getId());

        if (!item.getOwner().getId().equals(userId)) {

            log.info("[BookingServiceImpl.updateBookingApproved] вы не владелец вещи");

            throw new NotUserPermissionException("Пользователь не является владельцем");
        }

        booking.toggleApproved(approved);

        booking = bookingRepository.save(booking);

        log.info("[BookingServiceImpl.updateBookingApproved] в бронировании успешно изменен статус на: {}",
                (approved ? BookingStatus.APPROVED : BookingStatus.REJECTED).getStatusDescription());

        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public BookingDto findBookingById(Long id, Long userId) {

        Booking booking = findBookingEntityByIdOrThrowAnException(id);

        if (!isAllowed(booking, userId)) {
            log.info("[BookingServiceImpl.findBookingById] вы не можете просматривать информацио о бронировании");

            throw new NotUserPermissionException("Пользователь не может просматривать данные бронирование");
        }

        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> findBookingByState(Long userId, BookingState state, boolean byOwner) {

        userService.findUserEntityByIdOrThrowAnException(userId);

        Sort sortByStartDesc = Sort.by(Sort.Direction.DESC, "start");

        Specification<Booking> spec;

        if (byOwner) {
            spec = (root, query, cb) ->
                    cb.equal(root.get("item").get("owner").get("id"), userId);
        } else {
            spec = (root, query, cb) ->
                    cb.equal(root.get("booker").get("id"), userId);
        }

        List<Booking> booking = bookingRepository.findAll(spec.and(searchGenerator(state)), sortByStartDesc);

        log.info("[BookingServiceImpl.findBookingByState] получение бронировании по state: {}", state);

        return booking.stream()
                .map(BookingMapper::toBookingDto)
                .toList();
    }

    @Override
    public boolean existsBookingByUserIdAndItemId(Long itemId, Long userId) {
        return bookingRepository.existsByItemIdAndBookerIdAndStatusAndEndBefore(
                itemId, userId, BookingStatus.APPROVED, LocalDateTime.now()
        );
    }

    public Booking findBookingEntityByIdOrThrowAnException(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException("Бронирование с таким id не найдено"));
    }

    private boolean isAllowed(Booking booking, Long userId) {
        return booking.getBooker().getId().equals(userId) ||
                booking.getItem().getOwner().getId().equals(userId);
    }

    private Specification<Booking> searchGenerator(BookingState state) {

        LocalDateTime now = LocalDateTime.now();

        return (root, query, cb) -> switch (state) {
            case WAITING -> cb.equal(root.get("status"), BookingStatus.WAITING);
            case REJECTED -> cb.equal(root.get("status"), BookingStatus.REJECTED);
            case PAST -> cb.lessThan(root.get("end"), now);
            case FUTURE -> cb.greaterThan(root.get("start"), now);
            case CURRENT -> cb.and(cb.lessThan(root.get("start"), now), cb.greaterThan(root.get("end"), now));
            default -> null;
        };
    }
}
