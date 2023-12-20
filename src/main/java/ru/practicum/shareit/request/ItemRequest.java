package ru.practicum.shareit.request;

import lombok.Builder;
import lombok.Data;
import org.apache.catalina.User;

import java.util.Date;

@Data
@Builder
public class ItemRequest {
    private Long id;
    private String description;
    private User requestor;
    private Date created;
}
