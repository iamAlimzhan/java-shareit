package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoOwner;
import ru.practicum.shareit.booking.dto.InputBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import lombok.experimental.UtilityClass;
import ru.practicum.shareit.user.mapper.UserMapper;

@UtilityClass
public class BookingMapper {
    public Booking toBooking(InputBookingDto bookingDto) {
        Booking booking = new Booking();
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        Item item = new Item();
        item.setId(bookingDto.getItemId());
        booking.setItem(item);
        return booking;
    }

    public BookingDto toBookingDto(Booking booking) {
        return new BookingDto(booking.getId(), UserMapper.toUserDto(booking.getBooker()),
                ItemMapper.toItemDto(booking.getItem()), booking.getStart(), booking.getEnd(),
                booking.getStatus());
    }

    public BookingDtoOwner toBookingDtoForOwner(Booking booking) {
        if (booking == null) {
            return null;
        } else {
            return new BookingDtoOwner(booking.getId(), booking.getBooker().getId(),
                    booking.getStart(), booking.getEnd());
        }
    }
}
