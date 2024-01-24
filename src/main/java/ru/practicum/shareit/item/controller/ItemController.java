package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.HeaderConstants;
import ru.practicum.shareit.Update;
import ru.practicum.shareit.comment.dto.CommentCreateRequestDto;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreateRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;


    @GetMapping
    public List<ItemDto> getItemByUserId(@RequestHeader(HeaderConstants.X_SHARER_USER_ID) long userId) {
        return itemService.getItemsByUserId(userId);
    }

    @GetMapping("{itemId}")
    public ItemDto getItemById(@RequestHeader(HeaderConstants.X_SHARER_USER_ID) long userId, @PathVariable long itemId) {
        return itemService.getItemById(userId, itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> getItemsByText(@RequestParam String text) {
        return itemService.getItemsByText(text);
    }

    @PostMapping
    public ItemDto addItem(@RequestHeader(HeaderConstants.X_SHARER_USER_ID) long userId,
                           @Validated(Create.class) @RequestBody ItemCreateRequestDto itemDto) {
        return itemService.addItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(HeaderConstants.X_SHARER_USER_ID) long userId,
                              @PathVariable long itemId,
                              @Validated(Update.class) @RequestBody ItemCreateRequestDto itemDto) {
        return itemService.updateItem(userId, itemId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(HeaderConstants.X_SHARER_USER_ID) long userId,
                                 @PathVariable long itemId,
                                 @Valid @RequestBody CommentCreateRequestDto commentDto) {
        return itemService.addComment(userId, itemId, commentDto);
    }
}