package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.Update;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
public class UserDto {
    private Long id;
    @NotBlank(groups = {Create.class})
    @Size(max = 256, groups = {Create.class, Update.class})
    private String name;
    @NotEmpty(groups = {Create.class})
    @Email(groups = {Create.class, Update.class})
    @Size(max = 40, groups = {Create.class, Update.class})
    private String email;
}
