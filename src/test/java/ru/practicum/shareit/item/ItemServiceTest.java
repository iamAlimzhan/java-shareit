package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.BookingDtoOwner;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.status.BookingStatus;
import ru.practicum.shareit.comment.dto.CommentCreateRequestDto;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.mapper.CommentMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.ItemCreateRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.validation.constraints.Null;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {
    @Mock
    ItemRepository itemRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    BookingRepository bookingRepository;
    @Mock
    CommentRepository commentRepository;
    @Mock
    ItemRequestRepository itemRequestRepository;
    @Mock
    BookingService bookingService;
    @InjectMocks
    ItemServiceImpl itemService;

    private final User user = new User(null, "name", "name@mail.com");
    private final User owner = new User(2L, "name2", "name2@mail.kz");
    private final Item item = new Item(1L, "name3", "desc", true, owner,
            new ItemRequest());
    private final Booking booking = new Booking(1L, LocalDateTime.now().minusSeconds(120),
            LocalDateTime.now().minusSeconds(60), item, owner, BookingStatus.APPROVED);
    private final Comment comment = new Comment(1L, "text", item, user, LocalDateTime.now());
    private final int from = 0;
    private final int size = 10;
    private final PageRequest pageRequest = PageRequest.of(from, size);
    private final long userId = 1L;
    private final long itemId = 1L;

    @Test
    void getItemById() {
        when(bookingService.getNextBooking(itemId)).thenReturn(new BookingDtoOwner());
        when(bookingService.getLastBooking(itemId)).thenReturn(new BookingDtoOwner());
        when(commentRepository.findAllByItemId(itemId)).thenReturn(Collections.emptyList());
        when(itemRepository.checkItem(itemId)).thenReturn(item);

        ItemDto itemDto = itemService.getItemById(owner.getId(), itemId);

        assertNotNull(itemDto);
        assertEquals(itemDto.getId(), item.getId());
        assertEquals(itemDto.getName(), item.getName());
        assertEquals(itemDto.getDescription(), item.getDescription());
        verify(commentRepository, times(1)).findAllByItemId(anyLong());
    }

    @Test
    void getItemsByUserIdTest() {
        long userId = 1L;

        when(userRepository.checkUser(anyLong())).thenReturn(new User());
        when(itemRepository.findByOwnerId(anyLong(), any())).thenReturn(Collections.singletonList(new Item()));
        when(commentRepository.findAllByItemIn(any())).thenReturn(Collections.emptyList());

        when(bookingRepository.findAllByItemInAndStatus(any(), any())).thenReturn(Collections.emptyList());

        ItemServiceImpl itemService = new ItemServiceImpl(itemRepository, userRepository, bookingService,
                bookingRepository, commentRepository, itemRequestRepository);

        List<ItemDto> items = itemService.getItemsByUserId(userId, 0, 10);
        verify(bookingRepository, times(1)).findAllByItemInAndStatus(any(), any());
        verify(bookingRepository, times(0)).findAllByItemIn(any());
    }

    @Test
    void getItemsByText() {
        String text = "text";
        when(itemRepository.getItemByText(text.toLowerCase(), pageRequest)).thenReturn(List.of(item));

        List<ItemDto> itemDto = itemService.getItemsByText(text, from, size);

        assertNotNull(itemDto);
        assertEquals(1, itemDto.size());
        assertEquals(itemDto.get(0).getId(), item.getId());
        assertEquals(itemDto.get(0).getName(), item.getName());
        assertEquals(itemDto.get(0).getDescription(), item.getDescription());
        verify(itemRepository, times(1)).getItemByText(anyString(), any());
    }

    @Test
    void addItem() {
        when(itemRepository.save(any())).thenReturn(item);
        when(userRepository.checkUser(userId)).thenReturn(user);

        ItemDto itemDto = itemService.addItem(userId, ItemMapper.toItemCreateDto(item));

        assertNotNull(itemDto);
        assertEquals(itemDto.getId(), item.getId());
        assertEquals(itemDto.getName(), item.getName());
        assertEquals(itemDto.getDescription(), item.getDescription());
        verify(itemRepository, times(1)).save(any());
    }

    @Test
    void addItemWithEmptyName() {
        item.setName("");

        assertThrows(NullPointerException.class,
                () -> itemService.addItem(owner.getId(), ItemMapper.toItemCreateDto(item)));
    }

    @Test
    void addItemWithNullDescription() {
        item.setDescription(null);

        assertThrows(NullPointerException.class,
                () -> itemService.addItem(owner.getId(), ItemMapper.toItemCreateDto(item)));
    }

    /*@Test
    void addItemWithNullAvailable() {
        item.setAvailable(null);

        assertThrows(NullPointerException.class,
                () -> itemService.addItem(owner.getId(), ItemMapper.toItemCreateDto(item)));
    }*/

    @Test
    void updateItem() {
        when(itemRepository.save(any())).thenReturn(item);
        when(itemRepository.checkItem(itemId)).thenReturn(item);
        when(userRepository.checkUser(owner.getId())).thenReturn(owner);

        ItemDto itemDto = itemService.updateItem(owner.getId(), item.getId(), ItemMapper.toItemCreateDto(item));

        assertNotNull(itemDto);
        assertEquals(itemDto.getId(), item.getId());
        assertEquals(itemDto.getName(), item.getName());
        assertEquals(itemDto.getDescription(), item.getDescription());
        verify(itemRepository, times(1)).save(any());
    }

    /*@Test
    void updateItemWithEmptyName() {
        item.setName("");
        when(itemRepository.checkItem(itemId)).thenReturn(item);
        when(userRepository.checkUser(owner.getId())).thenReturn(owner);

        assertThrows(ValidationException.class,
                () -> itemService.updateItem(owner.getId(), item.getId(), ItemMapper.toItemCreateDto(item)));
    }*/

    @Test
    void updateItemWithEmptyDescription() {
        item.setDescription("");
        when(itemRepository.checkItem(itemId)).thenReturn(item);
        when(userRepository.checkUser(owner.getId())).thenReturn(owner);

        assertThrows(ValidationException.class,
                () -> itemService.updateItem(owner.getId(), item.getId(), ItemMapper.toItemCreateDto(item)));
    }

    @Test
    void updateItemWithNotValidOwner() {
        when(itemRepository.checkItem(itemId)).thenReturn(item);
        when(userRepository.checkUser(userId)).thenReturn(user);

        assertThrows(NotFoundException.class,
                () -> itemService.updateItem(userId, item.getId(), ItemMapper.toItemCreateDto(item)));
    }

   /* @Test
    void addComment() {
        when(commentRepository.save(any())).thenReturn(comment);
        when(bookingRepository.findFirstByItemIdAndBookerIdAndEndIsBeforeAndStatus(anyLong(), anyLong(), any(), any()))
                .thenReturn(booking);
        when(itemRepository.checkItem(itemId)).thenReturn(item);
        when(userRepository.checkUser(userId)).thenReturn(user);

        CommentDto commentDto = itemService.addComment(userId, itemId, CommentMapper.toCommentCreateDto(comment));

        assertNotNull(commentDto);
        assertEquals(commentDto.getId(), comment.getId());
        assertEquals(commentDto.getText(), comment.getText());
        verify(commentRepository, times(1)).save(any());
    }

    @Test
    void addCommentWithNotValidOwner() {
        when(itemRepository.checkItem(itemId)).thenReturn(item);
        when(userRepository.checkUser(userId)).thenReturn(user);
        when(bookingRepository.findFirstByItemIdAndBookerIdAndEndIsBeforeAndStatus(anyLong(), anyLong(), any(), any()))
                .thenReturn(null);

        assertThrows(ValidationException.class, () -> itemService.addComment(userId, itemId,
                CommentMapper.toCommentCreateDto(comment)));
        verify(bookingRepository, times(1)).existsByItemIdAndBookerIdAndEndIsBeforeAndStatus(any(), any(), any(), any());
    }*/


}
