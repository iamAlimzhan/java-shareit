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
        for (BookingPosition position : BookingPosition.values()) {
            if ((position.name().equalsIgnoreCase(value))) {
                return position;
            }
        }
        throw new IllegalStatusException("Unknown state: " + value);
    }
}