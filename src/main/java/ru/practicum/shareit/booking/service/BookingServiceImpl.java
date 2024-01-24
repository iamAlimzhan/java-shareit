package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoOwner;
import ru.practicum.shareit.booking.dto.InputBookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.position.BookingPosition;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.status.BookingStatus;
import ru.practicum.shareit.exceptions.IllegalStatusException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Service
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final Sort sortByStartDesc = Sort.by(Sort.Direction.DESC, "start");
    private final Sort sortByStartAsc = Sort.by(Sort.Direction.ASC, "start");

    @Override
    public BookingDto addBooking(long userId, InputBookingDto bookingDtoFrontend) {
        Item item = itemRepository.checkItem(bookingDtoFrontend.getItemId());
        if (!item.getAvailable()) {
            throw new ValidationException("Эта вещь не доступна для бронирования");
        }
        User booker = userRepository.checkUser(userId);
        if (userId == item.getOwner().getId()) {
            throw new NotFoundException(format("Пользователь с id = %s является владельцем вещи, "
                    + "поэтому не может брать вещь взаймы у себя", userId));
        }
        checkDate(bookingDtoFrontend);
        Booking booking = BookingMapper.toBooking(bookingDtoFrontend);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);
        bookingRepository.save(booking);
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public BookingDto updateBooking(long userId, long bookingId, boolean approved) {
        userRepository.checkUser(userId);
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new NotFoundException(format("Бронирования с id = %s нет в базе", bookingId)));
        long ownerId = booking.getItem().getOwner().getId();
        if (userId == ownerId) {
            if (approved) {
                if (BookingStatus.APPROVED.equals(booking.getStatus())) {
                    throw new ValidationException("Владелец уже одобрил бронирование");
                }
                booking.setStatus(BookingStatus.APPROVED);
            } else {
                if (BookingStatus.REJECTED.equals(booking.getStatus())) {
                    throw new ValidationException("Владелец уже отклонил бронирование");
                }
                booking.setStatus(BookingStatus.REJECTED);
            }
        } else {
            throw new NotFoundException(format("Пользователь с id = %s не является владельцем вещи "
                    + "и не имеет прав согласовывать бронирование", userId));
        }
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto findById(long bookingId, long userId) {
        userRepository.checkUser(userId);
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new NotFoundException(format("Запроса с id = %s нет в базе", bookingId)));
        long ownerId = booking.getItem().getOwner().getId();
        long bookerId = booking.getBooker().getId();
        if (userId != ownerId && userId != bookerId) {
            throw new NotFoundException(format("Пользователь с id = %s не имеет прав на просмотр бронирования вещи",
                    userId));
        }
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> findAllBookingsByUserId(long userId, BookingPosition position) {
        userRepository.checkUser(userId);
        List<Booking> bookings;
        switch (position) {
            case ALL:
                bookings = bookingRepository.findAllByBookerId(userId, sortByStartDesc);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfter(userId, LocalDateTime.now(),
                        LocalDateTime.now(), sortByStartDesc);
                break;
            case PAST:
                bookings = bookingRepository.findAllByBookerIdAndEndIsBefore(userId, LocalDateTime.now(),
                        sortByStartDesc);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByBookerIdAndStartIsAfter(userId, LocalDateTime.now(),
                        sortByStartDesc);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByBookerIdAndStatus(userId, BookingStatus.WAITING, sortByStartDesc);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByBookerIdAndStatus(userId, BookingStatus.REJECTED, sortByStartDesc);
                break;
            default:
                throw new IllegalStatusException("Unknown state: UNSUPPORTED_STATUS");
        }
        return bookings.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> findAllBookingsByOwner(long userId, BookingPosition position) {
        userRepository.checkUser(userId);
        List<Booking> bookings;
        switch (position) {
            case ALL:
                bookings = bookingRepository.findAllByItemOwnerId(userId, sortByStartDesc);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfter(userId,
                        LocalDateTime.now(), LocalDateTime.now(), sortByStartDesc);
                break;
            case PAST:
                bookings = bookingRepository.findAllByItemOwnerIdAndEndIsBefore(userId, LocalDateTime.now(),
                        sortByStartDesc);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByItemOwnerIdAndStartIsAfter(userId, LocalDateTime.now(),
                        sortByStartDesc);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByItemOwnerIdAndStatus(userId, BookingStatus.WAITING, sortByStartDesc);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByItemOwnerIdAndStatus(userId, BookingStatus.REJECTED, sortByStartDesc);
                break;
            default:
                throw new IllegalStatusException("Unknown state: UNSUPPORTED_STATUS");
        }
        return bookings.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }

    @Override
    public BookingDtoOwner getNextBooking(long itemId) {
        return BookingMapper.toBookingDtoForOwner(bookingRepository.findFirstByItemIdAndStartAfterAndStatus(itemId,
                LocalDateTime.now(), BookingStatus.APPROVED, sortByStartAsc));
    }

    @Override
    public BookingDtoOwner getLastBooking(long itemId) {
        return BookingMapper.toBookingDtoForOwner(
                bookingRepository.findFirstByItemIdAndStartLessThanEqualAndStatusOrderByStartDesc(
                        itemId, LocalDateTime.now(), BookingStatus.APPROVED, sortByStartAsc)
        );
    }


    private void checkDate(InputBookingDto bookingDtoFrontend) {
        if (bookingDtoFrontend.getEnd().isBefore(bookingDtoFrontend.getStart())) {
            throw new ValidationException("Время начала бронирования вещи не должна быть позже времени " +
                    "окончания бронирования");
        }
        if (bookingDtoFrontend.getEnd().isEqual(bookingDtoFrontend.getStart())) {
            throw new ValidationException("Время начала и окончания бронирования вещи не должны совпадать");
        }
    }
}
