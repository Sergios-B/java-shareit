package ru.practicum.shareit.item.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.MockGeneratorTest;
import ru.practicum.shareit.booking.exception.BookingNotFoundException;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.comment.CommentDto;
import ru.practicum.shareit.item.dto.comment.CreateCommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.service.impl.CommentServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserServiceImpl userService;

    @Mock
    private BookingService bookingService;

    @Mock
    private ItemService itemService;

    @InjectMocks
    private CommentServiceImpl commentService;

    @Test
    void should_returnException_whenBookingNotFoundByUserIdAndItemId() {

        Item item = Item.builder().id(1L).build();
        User user = User.builder().id(1L).build();
        CreateCommentDto commentDto = new CreateCommentDto();

        when(itemService.findItemEntityByIdOrThrowOnException(1L)).thenReturn(item);
        when(userService.findUserEntityByIdOrThrowAnException(1L)).thenReturn(user);
        when(bookingService.existsBookingByUserIdAndItemId(item.getId(), user.getId())).thenReturn(false);

        assertThrows(BookingNotFoundException.class, () ->
                commentService.addComment(item.getId(), user.getId(), commentDto));

        verifyNoInteractions(commentRepository);
    }

    @Test
    void should_returnComment_whenSuccessfullyAddComment() {

        String expectCommentText = MockGeneratorTest.generatorText(30);
        Long expectCommentId = 1L;

        Item item = Item.builder().id(1L).build();
        User user = User.builder().id(1L).name("Petia").build();
        CreateCommentDto commentDto = new CreateCommentDto();
        commentDto.setText(expectCommentText);
        Comment comment = Comment.builder().id(expectCommentId).item(item).author(user).text(expectCommentText).build();

        when(itemService.findItemEntityByIdOrThrowOnException(item.getId())).thenReturn(item);
        when(userService.findUserEntityByIdOrThrowAnException(user.getId())).thenReturn(user);
        when(bookingService.existsBookingByUserIdAndItemId(item.getId(), user.getId())).thenReturn(true);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentDto createdComment = commentService.addComment(item.getId(), user.getId(), commentDto);

        assertThat(createdComment)
                .isNotNull()
                .hasFieldOrPropertyWithValue("text", expectCommentText)
                .hasFieldOrPropertyWithValue("authorName", user.getName())
                .hasFieldOrPropertyWithValue("id", expectCommentId);

        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    void should_returnListComment_byItemIdsOrderCreatedAt() {

        int expectedCommentCount = 2;

        Item item = Item.builder().id(1L).build();

        List<Comment> comments = List.of(
                Comment.builder().id(1L).item(item).build(),
                Comment.builder().id(2L).item(item).build()
        );

        when(commentRepository.findAllByItemIdInOrderByCreatedAt(anyList())).thenReturn(comments);

        List<Comment> findComments = commentRepository.findAllByItemIdInOrderByCreatedAt(List.of(item.getId()));

        Assertions.assertThat(findComments)
                .hasSize(expectedCommentCount)
                .first()
                .hasFieldOrPropertyWithValue("id", comments.getFirst().getId())
                .hasFieldOrPropertyWithValue("item.id", item.getId());

        verify(commentRepository).findAllByItemIdInOrderByCreatedAt(List.of(item.getId()));
    }

    @Test
    void should_returnListComment_byOwnerId() {

        int expectedCommentCount = 2;

        User user = User.builder().id(1L).build();

        List<Comment> comments = List.of(
                Comment.builder().id(1L).author(user).build(),
                Comment.builder().id(2L).author(user).build()
        );

        when(commentRepository.findAllByItemOwnerId(any())).thenReturn(comments);

        List<Comment> findComments = commentRepository.findAllByItemOwnerId(user.getId());

        Assertions.assertThat(findComments)
                .hasSize(expectedCommentCount)
                .first()
                .hasFieldOrPropertyWithValue("id", comments.getFirst().getId())
                .hasFieldOrPropertyWithValue("author.id", user.getId());

        verify(commentRepository).findAllByItemOwnerId(user.getId());
    }
}