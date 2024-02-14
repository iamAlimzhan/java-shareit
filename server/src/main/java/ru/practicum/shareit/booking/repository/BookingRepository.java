package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.status.BookingStatus;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerId(long userId, PageRequest pageRequest);

    List<Booking> findAllByItemOwnerId(long userId, PageRequest pageRequest);

    List<Booking> findAllByBookerIdAndStartIsBeforeAndEndIsAfter(
            long userId,
            LocalDateTime startTime,
            LocalDateTime endTime,
            PageRequest pageRequest);

    List<Booking> findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfter(
            long userId,
            LocalDateTime startTime,
            LocalDateTime endTime,
            PageRequest pageRequest);

    List<Booking> findAllByBookerIdAndEndIsBefore(long userId, LocalDateTime time, PageRequest pageRequest);

    List<Booking> findAllByItemOwnerIdAndEndIsBefore(long userId, LocalDateTime time, PageRequest pageRequest);

    List<Booking> findAllByBookerIdAndStartIsAfter(long userId, LocalDateTime time, PageRequest pageRequest);

    List<Booking> findAllByItemOwnerIdAndStartIsAfter(long userId, LocalDateTime time, PageRequest pageRequest);

    List<Booking> findAllByBookerIdAndStatus(long userId, BookingStatus status, PageRequest pageRequest);

    List<Booking> findAllByItemOwnerIdAndStatus(long itemId, BookingStatus status, PageRequest pageRequest);

    Booking findFirstByItemIdAndStartLessThanEqualAndStatusOrderByStartDesc(
            long itemId,
            LocalDateTime time,
            BookingStatus status,
            Sort sort);

    Booking findFirstByItemIdAndStartAfterAndStatus(long itemId, LocalDateTime time, BookingStatus status, Sort sort);

    Booking findFirstByItemIdAndBookerIdAndEndIsBeforeAndStatus(
            long itemId,
            long userId,
            LocalDateTime time,
            BookingStatus status);

    boolean existsByItemIdAndBookerIdAndEndIsBeforeAndStatus(
            long itemId,
            long userId,
            LocalDateTime time,
            BookingStatus status);

    List<Booking> findAllByItemInAndStatus(List<Item> items, BookingStatus status);

    List<Booking> findAllByItemIn(List<Item> items);

}
