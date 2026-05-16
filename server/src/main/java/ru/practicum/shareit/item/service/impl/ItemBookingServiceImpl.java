package ru.practicum.shareit.item.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.item.ItemToOwnerDto;
import ru.practicum.shareit.item.exeption.ItemNotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.CommentService;
import ru.practicum.shareit.item.service.ItemBookingService;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemBookingServiceImpl implements ItemBookingService {

    private final ItemRepository itemRepository;
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final CommentService commentService;

    @Override
    public List<ItemToOwnerDto> findItemsWithAfterAndBeforeBookingDateByUserId(Long userId) {
        userService.findUserEntityByIdOrThrowAnException(userId);

        List<Item> items = itemRepository.findAllByOwnerId(userId);

        if (items.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> itemIds = items.stream().map(Item::getId).toList();
        LocalDateTime now = LocalDateTime.now();

        Map<Long, Booking> lastBooking = getLastBookingsByItemIds(itemIds, now);
        Map<Long, Booking> firstBooking = getFirstBookingByItemIds(itemIds, now);
        Map<Long, List<Comment>> comments = getCommentsByItemIds(itemIds);

        return items.stream()
                .map(item -> ItemMapper.toItemOwnerDto(
                        item,
                        firstBooking.getOrDefault(item.getId(), null),
                        lastBooking.getOrDefault(item.getId(), null),
                        comments.getOrDefault(item.getId(), List.of())
                ))
                .toList();
    }

    private Map<Long, Booking> getLastBookingsByItemIds(List<Long> ids, LocalDateTime now) {
        return bookingRepository
                .findAllByStatusNotAndStartLessThanAndItemIdInOrderByStartDesc(BookingStatus.REJECTED, now, ids)
                .stream().collect(Collectors.toMap(
                        b -> b.getItem().getId(),
                        b -> b,
                        (e1, e2) -> e1
                ));
    }

    private Map<Long, Booking> getFirstBookingByItemIds(List<Long> ids, LocalDateTime now) {
        return bookingRepository
                .findAllByStatusNotAndStartGreaterThanAndItemIdInOrderByStartAsc(BookingStatus.REJECTED, now, ids)
                .stream().collect(Collectors.toMap(
                        b -> b.getItem().getId(),
                        b -> b,
                        (e1, e2) -> e1
                ));
    }

    private Map<Long, List<Comment>> getCommentsByItemIds(List<Long> ids) {
        return commentService.findCommentsByItemIds(ids).stream()
                .collect(Collectors.groupingBy(comment -> comment.getItem().getId()));
    }

    @Override
    public ItemToOwnerDto getItemById(Long id, Long userId) { // ИСПРАВЛЕНО: Добавлен параметр userId
        Optional<Item> item = itemRepository.findById(id);

        if (item.isEmpty()) {
            throw new ItemNotFoundException("Вещь с таким id не найдена");
        }

        Item foundItem = item.get();
        LocalDateTime now = LocalDateTime.now();

        Booking lastBooking = null;
        Booking firstBooking = null;

        if (foundItem.getOwner().getId().equals(userId)) {
            Map<Long, Booking> lastBookingsMap = getLastBookingsByItemIds(List.of(id), now);
            Map<Long, Booking> firstBookingsMap = getFirstBookingByItemIds(List.of(id), now);

            lastBooking = lastBookingsMap.getOrDefault(id, null);
            firstBooking = firstBookingsMap.getOrDefault(id, null);
        }

        List<Comment> comments = commentService.findCommentsByItemIds(List.of(id));

        return ItemMapper.toItemOwnerDto(
                foundItem,
                firstBooking,
                lastBooking,
                comments
        );
    }
}
