package ru.practicum.shareit.item.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.exception.BookingNotFoundException;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.comment.CommentDto;
import ru.practicum.shareit.item.dto.comment.CreateCommentDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.service.CommentService;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final ItemService itemService;

    private final UserService userService;

    private final BookingService bookingService;

    private final CommentRepository commentRepository;

    @Override
    public CommentDto addComment(Long itemId, Long userId, CreateCommentDto commentDto) {

        Item item = itemService.findItemEntityByIdOrThrowOnException(itemId);

        User user = userService.findUserEntityByIdOrThrowAnException(userId);

        if (!bookingService.existsBookingByUserIdAndItemId(itemId, userId)) {

            log.info("[ItemServiceImpl.addComment] пользователь не может коментировать вещь которой не пользовался");

            throw new BookingNotFoundException("Бронирование с такой id вещи и id пользователя не обнаруженно");
        }

        Comment comment = CommentMapper.toComment(commentDto, item, user);

        log.info("[ItemServiceImpl.addComment] комментарий успешно добавлен успешно создана");

        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    public List<Comment> findCommentsByItemIds(List<Long> itemIds) {
        return commentRepository.findAllByItemIdInOrderByCreatedAt(itemIds);
    }

    @Override
    public List<Comment> findCommentsByOwnerId(Long ownerId) {
        return commentRepository.findAllByItemOwnerId(ownerId);
    }
}
