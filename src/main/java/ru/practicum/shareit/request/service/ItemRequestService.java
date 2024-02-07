package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto add(long userId, ItemRequestCreateDto itemRequestDto);

    List<ItemRequestDto> getAll(long userId, int from, int size);

    ItemRequestDto getById(long userId, long requestId);

    List<ItemRequestDto> getByUserId(long userId);
}
