package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
public class ItemDto {
    private Long id;
    @NotBlank(groups = Create.class)
    @Size(max = 256, groups = {Create.class, Update.class})
    private String name;
    @NotBlank(groups = Create.class)
    @Size(max = 1024, groups = {Create.class, Update.class})
    private String description;
    @NotNull(groups = Create.class)
    private Boolean available;
    private Long requestId;

    public interface Create {
    }

    public interface Update {
    }
}
