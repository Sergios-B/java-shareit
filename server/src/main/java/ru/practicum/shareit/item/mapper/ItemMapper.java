package ru.practicum.shareit.item.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.comment.CommentDto;
import ru.practicum.shareit.item.dto.item.CreateItemDto;
import ru.practicum.shareit.item.dto.item.ItemDto;
import ru.practicum.shareit.item.dto.item.ItemToOwnerDto;
import ru.practicum.shareit.item.dto.item.UpdateItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {

    public static Item toItem(CreateItemDto itemDto, User user) {
        return Item.builder()
                .owner(user)
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .requestId(itemDto.getRequestId() == null ? 0 : itemDto.getRequestId())
                .build();
    }

    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .available(item.isAvailable())
                .name(item.getName())
                .description(item.getDescription())
                .owner(UserMapper.toUserDto(item.getOwner()))
                .request(item.getRequestId())
                .build();
    }

    public static ItemToOwnerDto toItemOwnerDto(Item item,
                                                Booking firstBooking,
                                                Booking lastBooking,
                                                List<Comment> commentsList) {

        List<CommentDto> comments = commentsList.stream()
                .map(CommentMapper::toCommentDto).toList();

        return ItemToOwnerDto.builder()
                .id(item.getId())
                .available(item.isAvailable())
                .name(item.getName())
                .description(item.getDescription())
                .owner(UserMapper.toUserDto(item.getOwner()))
                .request(item.getRequestId())
                .nextBooking(BookingMapper.toBookingDto(firstBooking))
                .lastBooking(BookingMapper.toBookingDto(lastBooking))
                .comments(comments)
                .build();
    }

    public static Item toItemWithUpdateFields(Item item, UpdateItemDto itemDto) {

        if (itemDto.hasAvailable()) {
            item.setAvailable(itemDto.getAvailable());
        }

        if (itemDto.hasName()) {
            item.setName(itemDto.getName());
        }

        if (itemDto.hasDescription()) {
            item.setDescription(itemDto.getDescription());
        }

        return item;
    }
}
