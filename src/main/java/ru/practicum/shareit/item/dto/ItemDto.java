package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDtoOwner;
import ru.practicum.shareit.comment.dto.CommentDto;

import javax.validation.constraints.Size;
import java.util.List;

@Data
@Builder
public class ItemDto {
    private Long id;
    @Size(max = 256)
    private String name;
    @Size(max = 1024)
    private String description;
    private Boolean available;
    private Long requestId;
    private BookingDtoOwner lastBooking;
    private BookingDtoOwner nextBooking;
    private List<CommentDto> comments;
}
