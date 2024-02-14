package ru.practicum.shareit.booking.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.HeaderConstants;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.InputBookingDto;
import ru.practicum.shareit.booking.position.BookingPosition;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto addBooking(@RequestHeader(HeaderConstants.X_SHARER_USER_ID) long userId,
                                 @RequestBody InputBookingDto bookingDto) {
        return bookingService.addBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateBooking(@RequestHeader(HeaderConstants.X_SHARER_USER_ID) long userId,
                                    @PathVariable long bookingId, @RequestParam boolean approved) {
        return bookingService.updateBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto findById(@PathVariable long bookingId, @RequestHeader(HeaderConstants.X_SHARER_USER_ID) long userId) {
        return bookingService.findById(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> findAllBookingsByUserId(@RequestHeader(HeaderConstants.X_SHARER_USER_ID) long userId,
                                                    @RequestParam(defaultValue = "ALL", value = "state") BookingPosition state,
                                                    @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                    @RequestParam(defaultValue = "30") @Positive int size) {
        return bookingService.findAllBookingsByUserId(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> findAllBookingsByItemOwner(@RequestHeader(HeaderConstants.X_SHARER_USER_ID) long userId,
                                                       @RequestParam(defaultValue = "ALL") BookingPosition state,
                                                       @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                       @RequestParam(defaultValue = "30") @Positive int size) {
        return bookingService.findAllBookingsByOwner(userId, state, from, size);
    }
}
