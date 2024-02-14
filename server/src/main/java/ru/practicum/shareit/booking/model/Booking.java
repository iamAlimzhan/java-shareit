package ru.practicum.shareit.booking.model;

import ru.practicum.shareit.item.model.Item;
import lombok.*;
import ru.practicum.shareit.booking.status.BookingStatus;
import ru.practicum.shareit.user.model.User;


import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "bookings", schema = "public")
@AllArgsConstructor
@NoArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "start_date")
    private LocalDateTime start;
    @Column(name = "end_date")
    private LocalDateTime end;
    @JoinColumn(name = "item_id")
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Include
    private Item item;
    @JoinColumn(name = "booker_id")
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Include
    private User booker;
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private BookingStatus status;
}
