package ru.practicum.shareit.booking.position;

import ru.practicum.shareit.exceptions.IllegalStatusException;

public enum BookingPosition {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static BookingPosition parse(String value) {
        try {
            return BookingPosition.valueOf(value);
        } catch (Exception ex) {
            throw new IllegalStatusException("Unknown state: " + value);
        }
    }
}