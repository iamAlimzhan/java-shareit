package ru.practicum.shareit.booking;

import org.springframework.core.convert.converter.Converter;
import ru.practicum.shareit.booking.position.BookingPosition;
import ru.practicum.shareit.exceptions.IllegalStatusException;

public class BookingConverter implements Converter<String, BookingPosition> {
    @Override
    public BookingPosition convert(String source) {
        try {
            return BookingPosition.valueOf(source.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalStatusException("Unknown state: " + source);
        }
    }
}
