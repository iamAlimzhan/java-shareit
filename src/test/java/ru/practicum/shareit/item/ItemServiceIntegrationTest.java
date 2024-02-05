package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.status.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import java.awt.print.Book;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
public class ItemServiceIntegrationTest {
    private final EntityManager manager;
    private final ItemService service;

    private final User owner = new User(null, "name", "name@mail.com");
    private final User booker = new User(null, "name2", "name2@mail.kz");
    private final Item item = new Item(null, "name3", "desc", true, owner,
            null);
    private final Booking bookingLast = new Booking(null, LocalDateTime.now().minusMinutes(120),
            LocalDateTime.now().minusMinutes(60), item, booker, BookingStatus.APPROVED);
    private final Booking bookingNext = new Booking(null, LocalDateTime.now().plusMinutes(60),
            LocalDateTime.now().plusMinutes(120), item, booker, BookingStatus.APPROVED);

    @BeforeEach
    void setUp() {
        manager.persist(booker);
        manager.persist(owner);
        manager.persist(item);
        manager.persist(bookingLast);
        manager.persist(bookingNext);
    }

    @Test
    void getItemsByUserId() {
        int from = 0;
        int size = 10;

        List<ItemDto> items = service.getItemsByUserId(owner.getId(), from, size);

        assertNotNull(items);
        assertEquals(1, items.size());
        assertNotNull(items.get(0).getLastBooking());
        assertNotNull(items.get(0).getNextBooking());
        assertEquals(items.get(0).getId(), item.getId());
        assertEquals(items.get(0).getName(), item.getName());
        assertEquals(items.get(0).getDescription(), item.getDescription());
    }
}