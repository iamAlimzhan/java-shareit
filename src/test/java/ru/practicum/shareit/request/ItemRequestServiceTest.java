package ru.practicum.shareit.request;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceTest {
    @Mock
    ItemRequestRepository itemRequestRepository;
    @Mock
    ItemRepository itemRepository;
    @Mock
    UserRepository userRepository;
    @InjectMocks
    ItemRequestServiceImpl itemRequestService;

    private final User requester = new User(1L, "имя", "имя.name@mail.rk");
    private final User owner = new User(1L, "имя2", "name.667@mail.rk");
    private final ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "описание", null, null);
    private final ItemRequest itemRequest = new ItemRequest(1L, "описание", requester, null);
    private final Item item = new Item(1L, "ата аналардын", "бақыт", true, owner, itemRequest);
    private final Sort sort = Sort.by(Sort.Direction.DESC, "created");
    private final int from = 0;
    private final int size = 10;
    private final PageRequest pageRequest = PageRequest.of(from / size, size, sort);
    private final long userId = 1L;

    @Test
    void addTest() {
        ItemRequest itemRequest1 = ItemRequestMapper.toItemRequest(itemRequestDto, requester);
        when(itemRequestRepository.save(any())).thenReturn(itemRequest1);
        when(userRepository.checkUser(userId)).thenReturn(requester);

        ItemRequestDto actualItemRequestDto = itemRequestService.add(1L, itemRequestDto);

        assertNotNull(actualItemRequestDto);
        assertEquals(itemRequestDto.getId(), actualItemRequestDto.getId());
        assertEquals(itemRequestDto.getDescription(), actualItemRequestDto.getDescription());
        verify(itemRequestRepository, times(1)).save(any());
    }

    @Test
    void getByUserIdTest() {
        when(itemRepository.findAllByRequestIdIn(anyList()))
                .thenReturn(Collections.emptyList());
        when(itemRequestRepository.findAllByRequesterId(userId))
                .thenReturn(List.of(itemRequest));
        when(userRepository.checkUser(userId)).thenReturn(requester);

        List<ItemRequestDto> itemRequestDtoList = itemRequestService.getByUserId(userId);

        assertNotNull(itemRequestDtoList);
        assertEquals(1, itemRequestDtoList.size());
        verify(itemRequestRepository, times(1)).findAllByRequesterId(any());
    }

    @Test
    void getAllTest() {
        when(itemRepository.findAllByRequestIdIn(anyList()))
                .thenReturn(List.of(item));
        when(itemRequestRepository.findAllByRequesterIdNot(userId, pageRequest))
                .thenReturn(List.of(itemRequest));
        when(userRepository.checkUser(userId)).thenReturn(requester);

        List<ItemRequestDto> itemRequestDtoList = itemRequestService.getAll(userId, from, size);

        assertNotNull(itemRequestDtoList);
        assertEquals(1, itemRequestDtoList.size());
        verify(itemRequestRepository, times(1)).findAllByRequesterIdNot(anyLong(), any());
    }

    @Test
    @SneakyThrows
    void getByIdTest() {
        Long itemRequestId = itemRequest.getId();
        when(itemRepository.findAllByRequestId(anyLong()))
                .thenReturn(List.of(item));
        when(itemRequestRepository.findById(itemRequestId))
                .thenReturn(Optional.of(itemRequest));
        when(userRepository.checkUser(userId)).thenReturn(requester);

        ItemRequestDto actualItemRequestDto = itemRequestService.getById(requester.getId(), itemRequestId);

        assertNotNull(actualItemRequestDto);
        assertEquals(itemRequestId, actualItemRequestDto.getId());
        verify(itemRequestRepository, times(1)).findById(anyLong());
    }
}
