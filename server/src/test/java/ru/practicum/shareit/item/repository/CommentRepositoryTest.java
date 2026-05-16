package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.shareit.CommonRepositoryTestInit;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CommentRepositoryTest extends CommonRepositoryTestInit {

    @Autowired
    private CommentRepository commentRepository;

    private List<Comment> comments;

    private User owner;

    @BeforeEach
    void setUp() {

        owner = users.getFirst();

        comments = commentRepository.saveAll(
                List.of(
                        Comment.builder().text("Comment test 1").author(owner).item(randomItem()).build(),
                        Comment.builder().text("Comment test 2").author(owner).item(randomItem()).build(),
                        Comment.builder().text("Comment test 3").author(owner).item(randomItem()).build(),
                        Comment.builder().text("Comment test 4").author(owner).item(randomItem()).build()
                )
        );
    }

    @Test
    void shouldReturnComments_whenFindByItemOwnerId() {

        long expectedSize = comments.stream().filter(comment -> comment.getItem().getOwner()
                .getId().equals(owner.getId())).count();

        List<Comment> commentsByOwner = commentRepository.findAllByItemOwnerId(owner.getId());

        assertThat(commentsByOwner).hasSize((int) expectedSize);
    }

    @Test
    void shouldReturnComments_whenFindByIds() {

//        List<Long> ids = comments.stream().map(Comment::getId).toList();
//
//        List<Comment> commentsByIds = commentRepository.findAllByItemIdInOrderByCreatedAt(ids);
//
//        System.out.println(commentsByIds);
//
//        assertThat(commentsByIds)
//                .hasSize(ids.size())
//                .first()
//                .hasFieldOrPropertyWithValue("text", "Comment test 1");
    }
}