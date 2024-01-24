package ru.practicum.shareit.item.service;

import ru.practicum.shareit.comment.dto.CommentCreateRequestDto;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreateRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;


public interface ItemService {
    ItemDto addItem(long userId, ItemCreateRequestDto itemDto);

    ItemDto updateItem(long userId, long itemId, ItemCreateRequestDto itemDto);

    ItemDto getItemById(long userId, long itemId);

    List<ItemDto> getItemsByUserId(long userId);

    List<ItemDto> getItemsByText(String text);

    CommentDto addComment(long userId, long itemId, CommentCreateRequestDto commentDto);

}
