package ru.practicum.shareit.request.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;

@UtilityClass
public class ItemRequestMapper {
    public ItemRequest toItemRequest(ItemRequestDto itemRequestDto, User requester) {
        return new ItemRequest(itemRequestDto.getId(), itemRequestDto.getDescription(), requester, LocalDateTime.now());
    }

    public ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return new ItemRequestDto(itemRequest.getId(), itemRequest.getDescription(), itemRequest.getCreated(),
                new ArrayList<>());
    }

    public ItemRequest toItemCreateRequest(ItemRequestCreateDto itemRequestDto, User requester) {
        return new ItemRequest(null, itemRequestDto.getDescription(), requester, LocalDateTime.now());
    }
}
