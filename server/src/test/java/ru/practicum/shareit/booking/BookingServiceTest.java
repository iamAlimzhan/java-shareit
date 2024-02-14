package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoOwner;
import ru.practicum.shareit.booking.dto.InputBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.position.BookingPosition;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.booking.status.BookingStatus;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {
    private final User user = new User(null, "name", "name@mail.com");
    private final User owner = new User(2L, "name2", "name2@mail.kz");
    private final Item item = new Item(1L, "name3", "desc", true, owner,
            new ItemRequest());
    private final Booking booking = new Booking(1L, LocalDateTime.now().minusSeconds(120),
            LocalDateTime.now().minusSeconds(60), item, owner, BookingStatus.WAITING);
    private final InputBookingDto bookingDtoFrontend = new InputBookingDto(1L,
            LocalDateTime.now().minusSeconds(120), LocalDateTime.now().minusSeconds(60));
    private final int from = 0;
    private final int size = 10;
    private final long itemId = 1L;
    private final long bookingId = 1L;
    @Mock
    BookingRepository bookingRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    ItemRepository itemRepository;
    @InjectMocks
    BookingServiceImpl bookingService;
    long ownerId = owner.getId();

    @Test
    void findByIdTest() {
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(userRepository.checkUser(ownerId)).thenReturn(user);

        BookingDto bookingDto = bookingService.findById(bookingId, ownerId);

        assertNotNull(bookingDto);
        assertEquals(bookingDto.getId(), booking.getId());
        assertEquals(bookingDto.getBooker().getId(), booking.getBooker().getId());
        assertEquals(bookingDto.getItem().getId(), booking.getItem().getId());
        verify(bookingRepository, times(1)).findById(anyLong());
    }

    @Test
    void findByIdWithNotOwnerId() {
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(userRepository.checkUser(anyLong())).thenReturn(owner);

        assertThrows(NotFoundException.class,
                () -> bookingService.findById(bookingId, 3L));
    }

    @Test
    void findAllBookingsByUserIdWithStateAll() {
        BookingPosition position = BookingPosition.ALL;
        when(bookingRepository.findAllByBookerId(anyLong(), any())).thenReturn(List.of(booking));
        when(userRepository.checkUser(ownerId)).thenReturn(user);

        List<BookingDto> bookingDto = bookingService.findAllBookingsByUserId(ownerId, position, from, size);

        assertNotNull(bookingDto);
        assertEquals(1, bookingDto.size());
        assertEquals(bookingDto.get(0).getId(), booking.getId());
        assertEquals(bookingDto.get(0).getBooker().getId(), booking.getBooker().getId());
        assertEquals(bookingDto.get(0).getItem().getId(), booking.getItem().getId());
        verify(bookingRepository, times(1)).findAllByBookerId(anyLong(), any());
    }

    @Test
    void findAllBookingsByUserIdWithStateCurrent() {
        BookingPosition position = BookingPosition.CURRENT;
        when(bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfter(anyLong(), any(), any(), any()))
                .thenReturn(List.of(booking));
        when(userRepository.checkUser(ownerId)).thenReturn(user);

        List<BookingDto> bookingDto = bookingService.findAllBookingsByUserId(ownerId, position, from, size);

        assertNotNull(bookingDto);
        assertEquals(1, bookingDto.size());
        assertEquals(bookingDto.get(0).getId(), booking.getId());
        assertEquals(bookingDto.get(0).getBooker().getId(), booking.getBooker().getId());
        assertEquals(bookingDto.get(0).getItem().getId(), booking.getItem().getId());
        verify(bookingRepository, times(1)).findAllByBookerIdAndStartIsBeforeAndEndIsAfter(
                anyLong(), any(), any(), any());
    }

    @Test
    void findAllBookingsByUserIdWithStatePast() {
        BookingPosition position = BookingPosition.PAST;
        when(bookingRepository.findAllByBookerIdAndEndIsBefore(anyLong(), any(), any()))
                .thenReturn(List.of(booking));
        when(userRepository.checkUser(ownerId)).thenReturn(user);

        List<BookingDto> bookingDto = bookingService.findAllBookingsByUserId(ownerId, position, from, size);

        assertNotNull(bookingDto);
        assertEquals(1, bookingDto.size());
        assertEquals(bookingDto.get(0).getId(), booking.getId());
        assertEquals(bookingDto.get(0).getBooker().getId(), booking.getBooker().getId());
        assertEquals(bookingDto.get(0).getItem().getId(), booking.getItem().getId());
        verify(bookingRepository, times(1)).findAllByBookerIdAndEndIsBefore(anyLong(), any(),
                any());
    }

    @Test
    void findAllBookingsByUserIdWithStateFuture() {
        BookingPosition position = BookingPosition.FUTURE;
        when(bookingRepository.findAllByBookerIdAndStartIsAfter(anyLong(), any(), any()))
                .thenReturn(List.of(booking));
        when(userRepository.checkUser(ownerId)).thenReturn(user);

        List<BookingDto> bookingDto = bookingService.findAllBookingsByUserId(ownerId, position, from, size);

        assertNotNull(bookingDto);
        assertEquals(1, bookingDto.size());
        assertEquals(bookingDto.get(0).getId(), booking.getId());
        assertEquals(bookingDto.get(0).getBooker().getId(), booking.getBooker().getId());
        assertEquals(bookingDto.get(0).getItem().getId(), booking.getItem().getId());
        verify(bookingRepository, times(1)).findAllByBookerIdAndStartIsAfter(anyLong(), any(),
                any());
    }

    @Test
    void findAllBookingsByUserIdWithStateWaiting() {
        BookingPosition position = BookingPosition.WAITING;
        when(bookingRepository.findAllByBookerIdAndStatus(anyLong(), any(), any()))
                .thenReturn(List.of(booking));
        when(userRepository.checkUser(ownerId)).thenReturn(user);

        List<BookingDto> bookingDto = bookingService.findAllBookingsByUserId(ownerId, position, from, size);

        assertNotNull(bookingDto);
        assertEquals(1, bookingDto.size());
        assertEquals(bookingDto.get(0).getId(), booking.getId());
        assertEquals(bookingDto.get(0).getBooker().getId(), booking.getBooker().getId());
        assertEquals(bookingDto.get(0).getItem().getId(), booking.getItem().getId());
        verify(bookingRepository, times(1)).findAllByBookerIdAndStatus(anyLong(), any(), any());
    }

    @Test
    void findAllBookingsByUserIdWithStateRejected() {
        BookingPosition position = BookingPosition.REJECTED;
        when(bookingRepository.findAllByBookerIdAndStatus(anyLong(), any(), any()))
                .thenReturn(List.of(booking));
        when(userRepository.checkUser(ownerId)).thenReturn(user);

        List<BookingDto> bookingDto = bookingService.findAllBookingsByUserId(ownerId, position, from, size);

        assertNotNull(bookingDto);
        assertEquals(1, bookingDto.size());
        assertEquals(bookingDto.get(0).getId(), booking.getId());
        assertEquals(bookingDto.get(0).getBooker().getId(), booking.getBooker().getId());
        assertEquals(bookingDto.get(0).getItem().getId(), booking.getItem().getId());
        verify(bookingRepository, times(1)).findAllByBookerIdAndStatus(anyLong(), any(), any());
    }

    @Test
    void findAllBookingsByItemOwnerId_StateAll() {
        BookingPosition position = BookingPosition.ALL;
        when(bookingRepository.findAllByItemOwnerId(anyLong(), any())).thenReturn(List.of(booking));
        when(userRepository.checkUser(ownerId)).thenReturn(user);

        List<BookingDto> bookingDto = bookingService.findAllBookingsByOwner(ownerId, position, from, size);

        assertNotNull(bookingDto);
        assertEquals(1, bookingDto.size());
        assertEquals(bookingDto.get(0).getId(), booking.getId());
        assertEquals(bookingDto.get(0).getBooker().getId(), booking.getBooker().getId());
        assertEquals(bookingDto.get(0).getItem().getId(), booking.getItem().getId());
        verify(bookingRepository, times(1)).findAllByItemOwnerId(anyLong(), any());
    }

    @Test
    void findAllBookingsByItemOwnerIdWithStateCurrent() {
        BookingPosition position = BookingPosition.CURRENT;
        when(bookingRepository.findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfter(anyLong(), any(), any(), any()))
                .thenReturn(List.of(booking));
        when(userRepository.checkUser(ownerId)).thenReturn(user);

        List<BookingDto> bookingDto = bookingService.findAllBookingsByOwner(ownerId, position, from, size);

        assertNotNull(bookingDto);
        assertEquals(1, bookingDto.size());
        assertEquals(bookingDto.get(0).getId(), booking.getId());
        assertEquals(bookingDto.get(0).getBooker().getId(), booking.getBooker().getId());
        assertEquals(bookingDto.get(0).getItem().getId(), booking.getItem().getId());
        verify(bookingRepository, times(1)).findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfter(
                anyLong(), any(), any(), any());
    }

    @Test
    void findAllBookingsByItemOwnerIdWithStatePast() {
        BookingPosition position = BookingPosition.PAST;
        when(bookingRepository.findAllByItemOwnerIdAndEndIsBefore(anyLong(), any(), any()))
                .thenReturn(List.of(booking));
        when(userRepository.checkUser(ownerId)).thenReturn(user);

        List<BookingDto> bookingDto = bookingService.findAllBookingsByOwner(ownerId, position, from, size);

        assertNotNull(bookingDto);
        assertEquals(1, bookingDto.size());
        assertEquals(bookingDto.get(0).getId(), booking.getId());
        assertEquals(bookingDto.get(0).getBooker().getId(), booking.getBooker().getId());
        assertEquals(bookingDto.get(0).getItem().getId(), booking.getItem().getId());
        verify(bookingRepository, times(1)).findAllByItemOwnerIdAndEndIsBefore(anyLong(), any(),
                any());
    }

    @Test
    void findAllBookingsByItemOwnerIdWithStateFuture() {
        BookingPosition position = BookingPosition.FUTURE;
        when(bookingRepository.findAllByItemOwnerIdAndStartIsAfter(anyLong(), any(), any()))
                .thenReturn(List.of(booking));
        when(userRepository.checkUser(ownerId)).thenReturn(user);

        List<BookingDto> bookingDto = bookingService.findAllBookingsByOwner(ownerId, position, from, size);

        assertNotNull(bookingDto);
        assertEquals(1, bookingDto.size());
        assertEquals(bookingDto.get(0).getId(), booking.getId());
        assertEquals(bookingDto.get(0).getBooker().getId(), booking.getBooker().getId());
        assertEquals(bookingDto.get(0).getItem().getId(), booking.getItem().getId());
        verify(bookingRepository, times(1)).findAllByItemOwnerIdAndStartIsAfter(anyLong(), any(),
                any());
    }

    @Test
    void findAllBookingsByItemOwnerIdWithStateWaiting() {
        BookingPosition position = BookingPosition.WAITING;
        when(bookingRepository.findAllByItemOwnerIdAndStatus(anyLong(), any(), any()))
                .thenReturn(List.of(booking));
        when(userRepository.checkUser(ownerId)).thenReturn(user);

        List<BookingDto> bookingDto = bookingService.findAllBookingsByOwner(ownerId, position, from, size);

        assertNotNull(bookingDto);
        assertEquals(1, bookingDto.size());
        assertEquals(bookingDto.get(0).getId(), booking.getId());
        assertEquals(bookingDto.get(0).getBooker().getId(), booking.getBooker().getId());
        assertEquals(bookingDto.get(0).getItem().getId(), booking.getItem().getId());
        verify(bookingRepository, times(1)).findAllByItemOwnerIdAndStatus(anyLong(), any(), any());
    }

    @Test
    void findAllBookingsByItemOwnerIdWithStateRejected() {
        BookingPosition position = BookingPosition.REJECTED;
        when(bookingRepository.findAllByItemOwnerIdAndStatus(anyLong(), any(), any()))
                .thenReturn(List.of(booking));
        when(userRepository.checkUser(ownerId)).thenReturn(user);

        List<BookingDto> bookingDto = bookingService.findAllBookingsByOwner(ownerId, position, from, size);

        assertNotNull(bookingDto);
        assertEquals(1, bookingDto.size());
        assertEquals(bookingDto.get(0).getId(), booking.getId());
        assertEquals(bookingDto.get(0).getBooker().getId(), booking.getBooker().getId());
        assertEquals(bookingDto.get(0).getItem().getId(), booking.getItem().getId());
        verify(bookingRepository, times(1)).findAllByItemOwnerIdAndStatus(anyLong(), any(), any());
    }

    @Test
    void addTest() {
        long userId = 1L;
        when(bookingRepository.save(any())).thenReturn(booking);
        when(userRepository.checkUser(userId)).thenReturn(user);
        when(itemRepository.checkItem(itemId)).thenReturn(item);

        BookingDto bookingDto = bookingService.addBooking(userId, bookingDtoFrontend);

        assertNotNull(bookingDto);
        assertEquals(bookingDto.getBooker().getId(), user.getId());
        assertEquals(bookingDto.getItem().getId(), item.getId());
        verify(bookingRepository, times(1)).save(any());
    }

    @Test
    void addWithFalseAvailable() {
        item.setAvailable(false);
        when(itemRepository.checkItem(itemId)).thenReturn(item);

        assertThrows(ValidationException.class,
                () -> bookingService.addBooking(3L, bookingDtoFrontend));
    }

    @Test
    void updateTest() {
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(booking)).thenReturn(booking);
        when(userRepository.checkUser(ownerId)).thenReturn(owner);

        BookingDto bookingDto = bookingService.updateBooking(ownerId, bookingId, true);

        assertNotNull(bookingDto);
        assertEquals(bookingDto.getId(), booking.getId());
        assertEquals(bookingDto.getBooker().getId(), booking.getBooker().getId());
        assertEquals(bookingDto.getItem().getId(), booking.getItem().getId());
        verify(bookingRepository, times(1)).save(any());
    }

    @Test
    void updateWithAlreadyApprovedOwner() {
        booking.setStatus(BookingStatus.APPROVED);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(userRepository.checkUser(ownerId)).thenReturn(owner);

        assertThrows(ValidationException.class,
                () -> bookingService.updateBooking(ownerId, bookingId, true));
    }

    @Test
    void updateWithAlreadyRejectOwner() {
        booking.setStatus(BookingStatus.REJECTED);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(userRepository.checkUser(ownerId)).thenReturn(owner);

        assertThrows(ValidationException.class,
                () -> bookingService.updateBooking(ownerId, bookingId, false));
    }

    @Test
    void getLastBookingTest() {
        when(bookingRepository.findFirstByItemIdAndStartLessThanEqualAndStatusOrderByStartDesc(anyLong(), any(), any(), any()))
                .thenReturn(booking);

        BookingDtoOwner bookingDto = bookingService.getLastBooking(itemId);

        assertNotNull(bookingDto);
        assertEquals(bookingDto.getId(), booking.getId());
        assertEquals(bookingDto.getBookerId(), booking.getBooker().getId());
        verify(bookingRepository, times(1)).findFirstByItemIdAndStartLessThanEqualAndStatusOrderByStartDesc(anyLong(),
                any(), any(), any());
    }

    @Test
    void getNextBookingTest() {
        when(bookingRepository.findFirstByItemIdAndStartAfterAndStatus(anyLong(), any(), any(), any()))
                .thenReturn(booking);

        BookingDtoOwner bookingDto = bookingService.getNextBooking(itemId);

        assertNotNull(bookingDto);
        assertEquals(bookingDto.getId(), booking.getId());
        assertEquals(bookingDto.getBookerId(), booking.getBooker().getId());
        verify(bookingRepository, times(1)).findFirstByItemIdAndStartAfterAndStatus(anyLong(),
                any(), any(), any());
    }
}
