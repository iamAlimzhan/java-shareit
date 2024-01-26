package ru.practicum.shareit.booking;

import org.springframework.core.convert.converter.Converter;
import ru.practicum.shareit.booking.position.BookingPosition;
import ru.practicum.shareit.exceptions.IllegalStatusException;

public class BookingConverter implements Converter<String, BookingPosition> {
    @Override
    public BookingPosition convert(String source) {
        for (BookingPosition value : BookingPosition.values()) {
            if ((value.name().equalsIgnoreCase(source))) {
                return value;
            }
        }
        throw new IllegalStatusException("Unknown state: " + source);
    }
}
