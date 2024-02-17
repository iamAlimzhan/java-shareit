package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InputBookingDto {
    //@NotNull
    private Long itemId;
    //@NotNull
    //@FutureOrPresent
    private LocalDateTime start;
    //@NotNull
    //@Future
    private LocalDateTime end;
}
