package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDtoOwner;
import ru.practicum.shareit.comment.dto.CommentDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@Builder
public class ItemDto {
    private Long id;
    @NotBlank
    @Size(max = 256)
    private String name;
    @NotBlank
    @Size(max = 1024)
    private String description;
    @NotNull
    private Boolean available;
    private Long requestId;
    private BookingDtoOwner lastBooking;
    private BookingDtoOwner nextBooking;
    private List<CommentDto> comments;
}
