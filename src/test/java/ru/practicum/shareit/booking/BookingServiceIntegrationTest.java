package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.position.BookingPosition;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.status.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
public class BookingServiceIntegrationTest {
    private final EntityManager manager;
    private final BookingService service;

    private final User owner = new User(null, "name", "name@mail.com");
    private final User booker = new User(null, "name2", "name2@mail.kz");
    private final Item item = new Item(null, "name3", "desc", true, owner,
            null);
    private final Booking booking = new Booking(null, LocalDateTime.now().minusMinutes(120),
            LocalDateTime.now().minusMinutes(60), item, booker, BookingStatus.APPROVED);

    @BeforeEach
    void setUp() {
        manager.persist(booker);
        manager.persist(owner);
        manager.persist(item);
        manager.persist(booking);
    }

    @Test
    void findAllBookingsByUserId() {
        BookingPosition position = BookingPosition.ALL;
        int from = 0;
        int size = 10;

        List<BookingDto> bookings = service.findAllBookingsByUserId(booker.getId(), position, from, size);

        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertEquals(bookings.get(0).getId(), booking.getId());
        assertEquals(bookings.get(0).getBooker().getId(), booking.getBooker().getId());
        assertEquals(bookings.get(0).getItem().getId(), booking.getItem().getId());

    }
}
