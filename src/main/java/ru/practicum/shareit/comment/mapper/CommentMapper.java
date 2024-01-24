package ru.practicum.shareit.comment.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.comment.dto.CommentCreateRequestDto;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@UtilityClass
public class CommentMapper {
    public CommentDto toCommentDto(Comment comment) {
        return new CommentDto(comment.getId(), comment.getText(), comment.getAuthor().getName(), comment.getCreated());
    }

    public Comment toCommentCreate(CommentCreateRequestDto commentDto, Item item, User author) {
        return new Comment(null, commentDto.getText(), item, author, LocalDateTime.now());
    }

}
