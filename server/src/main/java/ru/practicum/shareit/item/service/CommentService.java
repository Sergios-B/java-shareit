package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.comment.CommentDto;
import ru.practicum.shareit.item.dto.comment.CreateCommentDto;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

public interface CommentService {

    CommentDto addComment(Long itemId, Long userId, CreateCommentDto commentDto);

    List<Comment> findCommentsByItemIds(List<Long> itemIds);

    List<Comment> findCommentsByOwnerId(Long ownerId);
}
