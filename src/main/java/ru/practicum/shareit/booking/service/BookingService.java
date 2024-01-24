package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoOwner;
import ru.practicum.shareit.booking.dto.InputBookingDto;
import ru.practicum.shareit.booking.position.BookingPosition;

import java.util.List;

public interface BookingService {
    BookingDto addBooking(long userId, InputBookingDto bookingDtoFrontend);

    BookingDto updateBooking(long userId, long bookingId, boolean approved);

    BookingDto findById(long bookingId, long userId);

    List<BookingDto> findAllBookingsByUserId(long userId, BookingPosition position);

    List<BookingDto> findAllBookingsByOwner(long userId, BookingPosition position);

    BookingDtoOwner getNextBooking(long itemId);

    BookingDtoOwner getLastBooking(long itemId);

}
