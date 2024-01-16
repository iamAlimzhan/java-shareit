package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDtoOwner;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.status.BookingStatus;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.mapper.CommentMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.validation.Validation;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final BookingService bookingService;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final Validation validation;


    @Override
    public ItemDto addItem(long userId, ItemDto itemDto) {
        User owner = validation.checkUser(userId);
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(owner);
        Item itemAfterSave = itemRepository.save(item);
        return ItemMapper.toItemDto(itemAfterSave);
    }

    @Override
    public ItemDto updateItem(long userId, long itemId, ItemDto itemDto) {
        Item item = validationUpdate(userId, itemId, itemDto);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto getItemById(long userId, long itemId) {
        Item item = validation.checkItem(itemId);
        ItemDto itemDto = ItemMapper.toItemDtoForOwner(item);
        if (userId == (item.getOwner().getId())) {
            itemDto.setLastBooking(bookingService.getLastBooking(itemId));
            itemDto.setNextBooking(bookingService.getNextBooking(itemId));
        } else {
            itemDto = ItemMapper.toItemDto(item);
        }
        itemDto.setComments(commentRepository.findAllByItemId(itemId).stream()
                .map(CommentMapper::toCommentDto).collect(Collectors.toList()));
        return itemDto;
    }

    //не совсем разобрался как разбить бронирования и комментарии на мапы и как испозльзовать их
    public List<ItemDto> getItemsByUserId(long userId) {
        validation.checkUser(userId);
        List<Item> items = itemRepository.findByOwnerId(userId);
        List<ItemDto> itemsDto = items.stream()
                .map(ItemMapper::toItemDtoForOwner).collect(Collectors.toList());
        List<Booking> bookings = bookingRepository.findAllByItemIn(items);
        List<Comment> comments = commentRepository.findAllByItemIn(items);
        for (ItemDto item : itemsDto) {
            List<Booking> bookingByItem = bookings.stream()
                    .filter(booking -> Objects.equals(booking.getItem().getId(), item.getId()))
                    .collect(Collectors.toList());
            item.setLastBooking(bookingByItem.stream()
                    .filter(booking -> booking.getStart().isBefore(LocalDateTime.now()))
                    .filter(booking -> Objects.equals(booking.getStatus(), BookingStatus.APPROVED))
                    .map(BookingMapper::toBookingDtoForOwner)
                    .max(Comparator.comparing(BookingDtoOwner::getEnd))
                    .orElse(null));
            item.setNextBooking(bookingByItem.stream()
                    .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                    .filter(booking -> Objects.equals(booking.getStatus(), BookingStatus.APPROVED))
                    .map(BookingMapper::toBookingDtoForOwner)
                    .min(Comparator.comparing(BookingDtoOwner::getStart))
                    .orElse(null));
            item.setComments(comments.stream()
                    .filter(comment -> Objects.equals(comment.getItem().getId(), item.getId()))
                    .map(CommentMapper::toCommentDto)
                    .collect(Collectors.toList()));
        }
        return itemsDto;
    }

    @Override
    public List<ItemDto> getItemsByText(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        List<Item> items = itemRepository.getItemByText(text.toLowerCase());
        return items.stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    public CommentDto addComment(long userId, long itemId, CommentDto commentDto) {
        User author = validation.checkUser(userId);
        Item item = validation.checkItem(itemId);
        checkAuthor(userId, itemId);
        Comment comment = commentRepository.save(CommentMapper.toComment(commentDto, author, item));
        return CommentMapper.toCommentDto(comment);
    }

    //не понял что и как исправить)
    private void checkAuthor(long userId, long itemId) {
        Booking booking = bookingRepository.findFirstByItemIdAndBookerIdAndEndIsBeforeAndStatus(itemId,
                userId, LocalDateTime.now(), BookingStatus.APPROVED);
        if (booking == null) {
            throw new ValidationException("Пользователь не может оставить комментарий");
        }
    }

    private Item validationUpdate(long userId, long itemId, ItemDto itemDto) {
        validation.checkUser(userId);
        Item item = validation.checkItem(itemId);
        User owner = item.getOwner();
        if (owner.getId() != userId) {
            throw new NotFoundException("Пользователь не является собственником вещи и не имеет прав на её изменение");
        }
        if (itemDto.getName() != null) {
            if (itemDto.getName().isBlank()) {
                throw new ValidationException("Имя вещи не должно быть пустым");
            }
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            if (itemDto.getDescription().isBlank()) {
                throw new ValidationException("Описание вещи не должно быть пустым");
            }
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        return item;
    }
}
