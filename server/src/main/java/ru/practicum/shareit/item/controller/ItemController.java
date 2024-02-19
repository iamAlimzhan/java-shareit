package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.HeaderConstants;
import ru.practicum.shareit.comment.dto.CommentCreateRequestDto;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreateRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;


    @GetMapping
    public List<ItemDto> getItemByUserId(@RequestHeader(HeaderConstants.X_SHARER_USER_ID) long userId,
                                         @RequestParam int from,
                                         @RequestParam int size) {
        return itemService.getItemsByUserId(userId, from, size);
    }

    @GetMapping("{itemId}")
    public ItemDto getItemById(@RequestHeader(HeaderConstants.X_SHARER_USER_ID) long userId, @PathVariable long itemId) {
        return itemService.getItemById(userId, itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> getItemsByText(@RequestParam String text,
                                        @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                        @RequestParam(defaultValue = "30") @Positive int size) {
        return itemService.getItemsByText(text, from, size);
    }

    @PostMapping
    public ItemDto addItem(@RequestHeader(HeaderConstants.X_SHARER_USER_ID) long userId,
                           @RequestBody ItemCreateRequestDto itemDto) {
        return itemService.addItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(HeaderConstants.X_SHARER_USER_ID) long userId,
                              @PathVariable long itemId,
                              @RequestBody ItemCreateRequestDto itemDto) {
        return itemService.updateItem(userId, itemId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(HeaderConstants.X_SHARER_USER_ID) long userId,
                                 @PathVariable long itemId,
                                 @RequestBody CommentCreateRequestDto commentDto) {
        return itemService.addComment(userId, itemId, commentDto);
    }
}