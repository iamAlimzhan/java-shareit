package ru.practicum.shareit.request.controller;

import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.HeaderConstants;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Validated
@AllArgsConstructor
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @GetMapping
    public List<ItemRequestDto> getByUserId(@RequestHeader(HeaderConstants.X_SHARER_USER_ID) long userId) {
        return itemRequestService.getByUserId(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAll(@RequestHeader(HeaderConstants.X_SHARER_USER_ID) long userId,
                                       @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero int from,
                                       @RequestParam(value = "size", defaultValue = "10") @Positive int size) {
        return itemRequestService.getAll(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getById(@RequestHeader(HeaderConstants.X_SHARER_USER_ID) long userId,
                                  @PathVariable long requestId) {
        return itemRequestService.getById(userId, requestId);
    }

    @PostMapping
    public ItemRequestDto add(@RequestHeader(HeaderConstants.X_SHARER_USER_ID) long userId,
                              @Valid @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestService.add(userId, itemRequestDto);
    }
}
