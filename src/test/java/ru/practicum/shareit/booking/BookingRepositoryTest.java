package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.status.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DataJpaTest
public class BookingRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BookingRepository repository;

    @Test
    void findAllByBookerId_NoBookingsFound() {
        List<Booking> bookings = repository.findAllByBookerId(-1L, PageRequest.of(0, 10));

        assertNotNull(bookings);
        assertTrue(bookings.isEmpty());
    }

    @Test
    void findAllByItemOwnerId_NoBookingsFound() {
        List<Booking> bookings = repository.findAllByItemOwnerId(-1L, PageRequest.of(0, 10));

        assertNotNull(bookings);
        assertTrue(bookings.isEmpty());
    }

    @Test
    void findAllByBookerIdAndStartIsBeforeAndEndIsAfter_NoBookingsFound() {
        List<Booking> bookings = repository.findAllByBookerIdAndStartIsBeforeAndEndIsAfter(
                -1L,
                LocalDateTime.now().plusMinutes(60),
                LocalDateTime.now().plusMinutes(120),
                PageRequest.of(0, 10)
        );

        assertNotNull(bookings);
        assertTrue(bookings.isEmpty());
    }


    @Test
    void findFirstByItemIdAndStartBeforeAndStatus_NoBookingFound() {
        Booking actualBooking = repository.findFirstByItemIdAndStartAfterAndStatus(
                -1L,
                LocalDateTime.now().minusMinutes(180),
                BookingStatus.APPROVED,
                Sort.by(Sort.Direction.DESC, "start")
        );

        assertNull(actualBooking);
    }

    @Test
    void findFirstByItemIdAndStartAfterAndStatus_NoBookingFound() {
        Booking actualBooking = repository.findFirstByItemIdAndStartAfterAndStatus(
                -1L,
                LocalDateTime.now().plusMinutes(180),
                BookingStatus.APPROVED,
                Sort.by(Sort.Direction.DESC, "start")
        );

        assertNull(actualBooking);
    }

    @Test
    void findFirstByItemIdAndBookerIdAndEndIsBeforeAndStatus_NoBookingFound() {
        Booking actualBooking = repository.findFirstByItemIdAndBookerIdAndEndIsBeforeAndStatus(
                -1L,
                -1L,
                LocalDateTime.now().minusMinutes(30),
                BookingStatus.APPROVED
        );

        assertNull(actualBooking);
    }

    @Test
    void existsByItemIdAndBookerIdAndEndIsBeforeAndStatus_NoBookingFound() {
        boolean exists = repository.existsByItemIdAndBookerIdAndEndIsBeforeAndStatus(
                -1L,
                -1L,
                LocalDateTime.now().minusMinutes(30),
                BookingStatus.APPROVED
        );

        assertFalse(exists);
    }
}

