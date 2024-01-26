package ru.practicum.shareit.comment.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
public class CommentCreateRequestDto {
    @NotBlank
    @Size(max = 1024)
    private String text;
}
