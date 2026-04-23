package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public ItemDto create(Long userId, ItemDto itemDto) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(owner);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    @Transactional
    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));
        if (!item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Вещь не принадлежит пользователю");
        }
        if (itemDto.getName() != null && !itemDto.getName().isBlank()) item.setName(itemDto.getName());
        if (itemDto.getDescription() != null && !itemDto.getDescription().isBlank())
            item.setDescription(itemDto.getDescription());
        if (itemDto.getAvailable() != null) item.setAvailable(itemDto.getAvailable());
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto findById(Long userId, Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        List<CommentDto> comments = commentRepository.findAllByItemId(itemId).stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());

        if (!item.getOwner().getId().equals(userId)) {
            return ItemMapper.toItemDto(item, null, null, comments);
        }

        return fillAdditionalInfo(item, comments);
    }

    @Override
    public Collection<ItemDto> findAllByOwner(Long userId) {
        return itemRepository.findAllByOwnerId(userId).stream()
                .map(item -> {
                    List<CommentDto> comments = commentRepository.findAllByItemId(item.getId()).stream()
                            .map(CommentMapper::toCommentDto)
                            .collect(Collectors.toList());
                    return fillAdditionalInfo(item, comments);
                })
                .sorted(Comparator.comparing(ItemDto::getId))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ItemDto> search(String text) {
        if (text == null || text.isBlank()) return new ArrayList<>();
        return itemRepository.search(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        LocalDateTime now = LocalDateTime.now();
        boolean hasFinishedBooking = bookingRepository.existsByBookerIdAndItemIdAndStatusAndEndBefore(
                userId, itemId, BookingStatus.APPROVED, now);

        if (!hasFinishedBooking) {
            throw new ValidationException("Вы не можете оставить комментарий, так как не арендовали эту вещь");
        }

        User author = userRepository.findById(userId).orElseThrow();
        Item item = itemRepository.findById(itemId).orElseThrow();
        Comment comment = CommentMapper.toComment(commentDto, item, author);

        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    private ItemDto fillAdditionalInfo(Item item, List<CommentDto> comments) {
        LocalDateTime now = LocalDateTime.now();

        Booking lastBooking = bookingRepository
                .findFirstByItemIdAndStatusAndStartBeforeOrderByEndDesc(item.getId(), BookingStatus.APPROVED, now)
                .orElse(null);

        Booking nextBooking = bookingRepository
                .findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(item.getId(), BookingStatus.APPROVED, now)
                .orElse(null);

        ItemDto.BookingShortDto last = (lastBooking != null) ?
                ItemDto.BookingShortDto.builder().id(lastBooking.getId()).bookerId(lastBooking.getBooker().getId()).build() : null;

        ItemDto.BookingShortDto next = (nextBooking != null) ?
                ItemDto.BookingShortDto.builder().id(nextBooking.getId()).bookerId(nextBooking.getBooker().getId()).build() : null;

        return ItemMapper.toItemDto(item, last, next, comments);
    }
}