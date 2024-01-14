package ru.practicum.shareit.booking.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.HeaderConstants;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.InputBookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@AllArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto addBooking(@RequestHeader(HeaderConstants.X_SHARER_USER_ID) long userId,
                          @Valid @RequestBody InputBookingDto bookingDto) {
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
                                                    @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.findAllBookingsByUserId(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> findAllBookingsByItemOwner(@RequestHeader(HeaderConstants.X_SHARER_USER_ID) long userId,
                                                       @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.findAllBookingsByOwner(userId, state);
    }
}
