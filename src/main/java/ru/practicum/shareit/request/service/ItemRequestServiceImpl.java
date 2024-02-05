package ru.practicum.shareit.request.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Service
@AllArgsConstructor
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemRequestDto add(long userId, ItemRequestDto itemRequestDto) {
        User requester = userRepository.checkUser(userId);
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto, requester);
        return ItemRequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestDto> getAll(long userId, int from, int size) {
        userRepository.checkUser(userId);
        Sort sort = Sort.by(Sort.Direction.DESC, "created");
        PageRequest pageRequest = PageRequest.of(from / size, size, sort);
        List<ItemRequest> itemRequestList = itemRequestRepository.findAllByRequesterIdNot(userId, pageRequest);
        if (itemRequestList.isEmpty()) {
            return Collections.emptyList();
        }
        return addItemsInRequest(itemRequestList);
    }

    @Override
    public ItemRequestDto getById(long userId, long requestId) {
        userRepository.checkUser(userId);
        ItemRequest itemRequest = itemRequestRepository.findById(requestId).orElseThrow(() ->
                new NotFoundException(format("Запрос с id = %s не найден", requestId)));
        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
        List<ItemDto> itemDtoList = itemRepository.findAllByRequestId(requestId).stream()
                .map(ItemMapper::toItemDto).collect(Collectors.toList());
        if (!itemDtoList.isEmpty()) {
            itemRequestDto.setItems(itemDtoList);
        }
        return itemRequestDto;
    }

    @Override
    public List<ItemRequestDto> getByUserId(long userId) {
        userRepository.checkUser(userId);
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequesterId(userId);
        return addItemsInRequest(itemRequests);
    }


    private List<ItemRequestDto> addItemsInRequest(List<ItemRequest> itemRequests) {
        List<Long> itemRequestIds = itemRequests.stream().map(ItemRequest::getId).collect(Collectors.toList());
        List<ItemDto> itemDtoList = itemRepository.findAllByRequestIdIn(itemRequestIds).stream().map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
        List<ItemRequestDto> requestDtoList = itemRequests.stream().map(ItemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
        for (ItemRequestDto itemRequestDto : requestDtoList) {
            itemRequestDto.setItems(itemDtoList.stream()
                    .filter(i -> Objects.equals(i.getRequestId(), itemRequestDto.getId()))
                    .collect(Collectors.toList()));
        }
        return requestDtoList;
    }
}
