package ru.practicum.shareit.item;

import io.micrometer.core.instrument.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.header.HeaderConstants;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collections;

@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> getAllByUserId(
            @RequestHeader(HeaderConstants.X_SHARER_USER_ID) @Positive long userId,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size) {
        return itemClient.getItems(userId, from, size);
    }

    @GetMapping("{itemId}")
    public ResponseEntity<Object> getById(@RequestHeader(HeaderConstants.X_SHARER_USER_ID) @Positive long userId,
                                          @PathVariable Long itemId) {
        return itemClient.findItemById(userId, itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getItemsByText(
            @RequestHeader(HeaderConstants.X_SHARER_USER_ID) @Positive long userId,
            @RequestParam String text,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size) {
        if (StringUtils.isBlank(text)) {
            return ResponseEntity.ok(Collections.emptyList());
        } else {
            return itemClient.findItemsByText(userId, text, from, size);
        }
    }

    @PostMapping
    public ResponseEntity<Object> addItem(@RequestHeader(HeaderConstants.X_SHARER_USER_ID) @Positive Long userId,
                                          @Validated(ItemDto.Create.class) @RequestBody ItemDto itemDto) {
        return itemClient.addItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> patchItem(@RequestHeader(HeaderConstants.X_SHARER_USER_ID) @Positive Long userId,
                                            @Validated(ItemDto.Update.class) @RequestBody ItemDto itemDto,
                                            @PathVariable("itemId") Long itemId) {
        return itemClient.patchItem(userId, itemId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(HeaderConstants.X_SHARER_USER_ID) @Positive Long userId,
                                             @Valid @RequestBody CommentDto commentDto,
                                             @PathVariable("itemId") Long itemId) {
        return itemClient.addComment(userId, itemId, commentDto);
    }
}
