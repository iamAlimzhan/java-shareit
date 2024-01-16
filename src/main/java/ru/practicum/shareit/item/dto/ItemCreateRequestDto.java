package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
public class ItemCreateRequestDto {
    @NotBlank
    @Size(max = 256)
    private String name;
    @NotBlank
    @Size(max = 1024)
    private String description;
    @NotNull
    private Boolean available;
}
