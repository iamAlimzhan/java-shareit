package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoOwner;
import ru.practicum.shareit.booking.dto.InputBookingDto;

import java.util.List;

public interface BookingService {
    BookingDto addBooking(long userId, InputBookingDto bookingDtoFrontend);

    BookingDto updateBooking(long userId, long bookingId, boolean approved);

    BookingDto findById(long bookingId, long userId);

    List<BookingDto> findAllBookingsByUserId(long userId, String position);

    List<BookingDto> findAllBookingsByOwner(long userId, String position);

    BookingDtoOwner getNextBooking(long itemId);

    BookingDtoOwner getLastBooking(long itemId);

}
