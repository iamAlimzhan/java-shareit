package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemDto {
    //не понял как работают эти аннотации и как их использовать правильно
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long requestId;
}
